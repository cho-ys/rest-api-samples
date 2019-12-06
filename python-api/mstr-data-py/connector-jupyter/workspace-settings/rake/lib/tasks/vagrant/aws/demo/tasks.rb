require 'vagrant/rake/provider/aws'

provider = Vagrant::Rake::Provider::AWS.new

provider.generate_tasks

task :rt_deploy, [:version] => [:"rt_deploy_connector_jupyter_centos_#{Git.branch_name}_demo"]
task :"rt_deploy_connector_jupyter_centos_#{Git.branch_name}_demo", [:version] => [:download_artifact]
