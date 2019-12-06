require "deep_merge"
require "vagrant/project/machine/base"
require "vagrant/project/machine/config/base"
require "vagrant/project/mixins/configurable"
require 'logging-helper'

module Vagrant
  module Project
    module Machine
      class ConnectorJupyterWindows < Base
        class Configuration < Vagrant::Project::Machine::Config::Base
          include LoggingHelper::LogToTerminal

          def initialize
            Berkshelf::Berksfile.preposition_berksfile(File.expand_path('connector_jupyter.berks', File.dirname(__FILE__)))
          end

          def configure_this(provisioner)
            artifact_name = $WORKSPACE_SETTINGS[:vagrant][:boxes][:windows][:name]
            artifact_version = $WORKSPACE_SETTINGS[:vagrant][:boxes][:windows][:version]
            provider.box_from_nexus(artifact_name, artifact_version)

            provider.os_name 'windows'
            provider.os_version '2008R2'

            vagrant_machine.vm.communicator = "winrm"
            vagrant_machine.vm.guest = :windows
            vagrant_machine.vm.network :forwarded_port, guest: 3389, host: 3389, id: "rdp", auto_correct: true
            vagrant_machine.winrm.password = 'vagrant'

            provisioner.configure{|chef|
              chef.add_recipe "connector_jupyter"
              chef.json = {
                connector_jupyter: {
                  
                }
              }
            }
          end


        end

        register :machine, :connector_jupyter_windows, self.inspect

        def configuration_class
          Vagrant::Project::Machine::ConnectorJupyterWindows::Configuration
        end

        def provisioner_class
          require 'vagrant/project/provisioner/chef'
          Vagrant::Project::Provisioner::Chef
        end

      end
    end
  end
end
