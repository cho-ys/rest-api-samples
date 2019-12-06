require "deep_merge"
require "vagrant/project/machine/base"
require "vagrant/project/machine/config/base"
require "vagrant/project/mixins/configurable"
require 'logging-helper'
require 'uri'

module Vagrant
  module Project
    module Machine
      class ConnectorJupyterCentos < Base
        class Configuration < Vagrant::Project::Machine::Config::Base
          include LoggingHelper::LogToTerminal

          def initialize
            Berkshelf::Berksfile.preposition_berksfile(File.expand_path('connector_jupyter.berks', File.dirname(__FILE__)))
          end

          def configure_this(provisioner)
            artifact_name = $WORKSPACE_SETTINGS[:vagrant][:boxes][:centos][:name]
            artifact_version = $WORKSPACE_SETTINGS[:vagrant][:boxes][:centos][:version]
            provider.box_from_nexus(artifact_name, artifact_version)

            provider.os_name 'centos'
            provider.os_version '6.7'

            provisioner.configure{|chef|
              chef.add_recipe "connector_jupyter"
              chef.json = {
                connector_jupyter: {
                  
                }
              }
            }
          end

        end

        register :machine, :connector_jupyter_centos, self.inspect

        def configuration_class
          Vagrant::Project::Machine::ConnectorJupyterCentos::Configuration
        end

        def provisioner_class
          require 'vagrant/project/provisioner/chef'
          Vagrant::Project::Provisioner::Chef
        end

      end
    end
  end
end
