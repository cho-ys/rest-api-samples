require 'vagrant/rake/provider/aws'

provider = Vagrant::Rake::Provider::AWS.new

provider.generate_tasks

task :"rt_deploy_connector_jupyter_centos_#{Git.branch_name}_dev" => [:move_artifact]