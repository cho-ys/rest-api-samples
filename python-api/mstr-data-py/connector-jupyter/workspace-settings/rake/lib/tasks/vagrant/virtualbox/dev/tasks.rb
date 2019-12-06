
__END__
below is an example of quick deploy

require 'ssh-executor'
require 'fileutils'

Rake::application.remove_task(:"rt_deploy_#{$WORKSPACE_SETTINGS[:project][:name].gsub(/-/, '_')}")

[
  'centos'#,
  #'windows'
].each{|os_suffix|
  machine_name = :"#{$WORKSPACE_SETTINGS[:project][:name].gsub(/-/, '_')}_#{os_suffix}"
  deploy_task_name = :"rt_deploy_#{machine_name}"
  Rake::Task[deploy_task_name].clear_actions

  multitask deploy_task_name, [:vagrant_opts] do |task, args|
    vagrant_opts = []
    vagrant_opts = args.vagrant_opts.chomp.split(" ") if args.has_key?(:vagrant_opts)

    dual_deploy = DualDeploy.new(machine_name, $WORKSPACE_SETTINGS[:project][:name].gsub(/-/, '_'))

    dual_deploy.deploy(vagrant_opts)
  end

  task deploy_task_name => [:move_artifact]
}

class DualDeploy

  attr_reader :machine_name,
    :cookbook_name,
    :service_name,
    :guest_home_dir,
    :host_home_dir,
    :source_artifact_path,
    :deployed_artifact_path

  def initialize(machine_name, cookbook_name)
    @machine_name = machine_name.to_s.to_sym
    @cookbook_name = cookbook_name.to_s.to_sym

    @service_name = $WORKSPACE_SETTINGS[:machine_report][@machine_name][:provisioners][:"chef_solo_#{@machine_name}"][:config][:json][@cookbook_name][:service]

    @guest_home_dir = $WORKSPACE_SETTINGS[:machine_report][@machine_name][:provisioners][:"chef_solo_#{@machine_name}"][:config][:json][@cookbook_name][:home_dir]
    synced_folder = $WORKSPACE_SETTINGS[:machine_report][@machine_name][:provider][:synced_folders].find{|synced_folder|
      synced_folder[:guest_path] == File.dirname(guest_home_dir)
    }
    raise "could not find a synced folder with a guest path of:

  #{File.dirname(guest_home_dir)}

this is used for a quick deploy to copy artifacts directly to the VM
the value should probably look like:

  /opt/microstrategy

" if synced_folder.nil?
    
    @host_home_dir = "#{synced_folder[:host_path]}/#{File.basename(guest_home_dir)}"

    @source_artifact_path = "#{$WORKSPACE_SETTINGS[:paths][:project][:deploy][:chef][:cookbook][:home]}/#{@cookbook_name}/files/#{$WORKSPACE_SETTINGS[:machine_report][@machine_name][:provisioners][:"chef_solo_#{@machine_name}"][:config][:json][@cookbook_name][:source]}"
    @deployed_artifact_path = "#{@host_home_dir}/lib/#{$WORKSPACE_SETTINGS[:machine_report][@machine_name][:provisioners][:"chef_solo_#{@machine_name}"][:config][:json][@cookbook_name][:file_name]}"
  end

  def deploy(vagrant_opts)
    if quick_deploy?
      execute_quick_deploy
    else
      execute_full_deploy(vagrant_opts)
    end
  end

  def quick_deploy?
    do_a_quick_deploy = false
    if !provider.machine_state_exist?(machine_name)
      debug { "Not performing a quick deploy: machine rest does not exist" }
      do_a_quick_deploy = false
    else
      provider.up_machine(machine_name)
      if provider.current_snapshot(machine_name)[:description][:post_deploy] == false
        debug { "Not performing a quick deploy: current snapshot is not of a successful deployment" }
        do_a_quick_deploy = false
      else
        do_a_quick_deploy = provider.current_compared_to_snapshot?(machine_name, [{file: source_artifact_path}])
        if !do_a_quick_deploy
          debug { "Not performing a quick deploy: infrastructure automation has changed" }
        end
      end
    end
    if do_a_quick_deploy
      debug { "Performing a quick deploy" }
    end

    do_a_quick_deploy
  end

  def execute_quick_deploy
    puts %/
#{divider}

      performing a quick deploy to vagrant machine #{machine_name}

#{divider}
/
    
    stop_service
    copy_artifact
    start_service
  end

  def execute_full_deploy(vagrant_opts)
        puts %/
#{divider}

      performing a full deploy to vagrant machine #{machine_name}

#{divider}
/
    
    provider.provision_machine(machine_name, vagrant_opts)
    provider.snapshot_machine(machine_name, provider.deployment_time)
  end

  def copy_artifact
    FileUtils.rm_f(deployed_artifact_path) if File.exist?(deployed_artifact_path)
    FileUtils.cp(source_artifact_path, deployed_artifact_path)
  end

  def ssh_connection
    return @ssh_connection unless @ssh_connection.nil?

    ssh_ip_address = $WORKSPACE_SETTINGS[:machine_report][machine_name][:ssh_info][:host]
    ssh_user = $WORKSPACE_SETTINGS[:machine_report][machine_name][:ssh_info][:username]
    ssh_private_key = $WORKSPACE_SETTINGS[:machine_report][machine_name][:ssh_info][:private_key_path][0]
    ssh_port = $WORKSPACE_SETTINGS[:machine_report][machine_name][:ssh_info][:port]

    @ssh_connection = SSHHelper::SSHExecutor.new(ssh_ip_address, ssh_user, ssh_private_key, port: ssh_port)
  end

  attr_writer :stop_script
  def stop_script
    if @stop_script.nil?
      @stop_script = <<-EOS
        set -e

        if [[ -e "/etc/init.d/#{service_name}" ]] ; then
          echo "service #{service_name} is installed"
          if service #{service_name} status ; then
            echo "service #{service_name} is running"
            service #{service_name} stop
          else
            echo "service #{service_name} is not running"
          fi
        else
          echo "service #{service_name} is not installed"
        fi
      EOS
    end
    return @stop_script
  end

  def stop_service
    ssh_connection.execute_script! stop_script
  end

  attr_writer :start_script
  def start_script
    if @start_script.nil?
      @start_script = <<-EOS
        set -e
        
        if [[ -e "/etc/init.d/#{service_name}" ]] ; then
          echo "service #{service_name} is installed"
          if ! service #{service_name} status ; then
            echo "service #{service_name} is not running"
            service #{service_name} start
          else
            echo "service #{service_name} is already running, hmmm that is strange???"
          fi
        else
          >&2 echo "error: the service #{service_name} was not found, something is wrong. I think a full deploy is needed."
          exit -1
        fi
      EOS
    end
    return @start_script
  end

  def start_service
    ssh_connection.execute_script! start_script
  end
end