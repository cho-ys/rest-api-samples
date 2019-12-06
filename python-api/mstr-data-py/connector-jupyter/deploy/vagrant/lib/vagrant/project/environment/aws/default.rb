require "deep_merge"
require "vagrant/project/environment/base"
require 'vagrant/project/mixins/tagging'
require 'vagrant/project/provisioner/chef'
require "vagrant/project/mixins/configurable"

module Vagrant
  module Project
    module Environment
      module AWS
        class Default < Vagrant::Project::Environment::Base
          include Vagrant::Project::Mixins::Tagging
          include Vagrant::Project::Mixins::Configurable

          register :environment, :aws, self.inspect
          attr_config :env_tags

          def configure_provider(machine, &block)
            machine.provider.set_defaults
            env_tags = { Division: 'DevOps' }

            machine.provider.configuration.with{

              ami 'ami-aa51c3bc' if ami.nil?
              tags env_tags
            }

            block.call()
          end

          def configure_provisioner(machine, &block)
            return nil unless machine.provisioner_class == Vagrant::Project::Provisioner::Chef
            Berkshelf::Berksfile.preposition_berksfile(File.expand_path('default.berks', File.dirname(__FILE__)))

            machine.provisioner.set_defaults do |chef|
              chef.file_cache_path = '/var/chef/cache/artifacts'

              chef.add_recipe 'chef_commons'
            end

            case machine.vagrant_machine.vm.guest
            when :windows

            else
              machine.provisioner.configure do |chef|
                chef.add_recipe 'dev_commons::hosts'
                chef.add_recipe 'yum_mirror::repo'
                chef.add_recipe 'yum'
                chef.add_recipe 'timezone-ii'
                chef.add_recipe 'ntp'
              end
            end

            machine.provisioner.configure do |chef|
              block.call()
            end

            machine.provisioner.configure do |chef|

              os_version = machine.provider.configuration.os_version
              chef.json.deep_merge!({
                data_bag_secret: 'Z91%gDUDF5!jz0UE3ZC$9VR!o892',
                ec2: {
                  # tags: set_tags(name: machine.name)
                },
                yum_mirror: {
                  mirrors: [
                    {
                      name: ('microstrategy-rhel-6-extra' if os_version.start_with? '6.') || ('microstrategy-rhel-7-extras' if os_version.start_with? '7.'),
                      url:  ('https://yum.internal.microstrategy.com/extra' if os_version.start_with? '6.') || ('https://yum.internal.microstrategy.com/extras-7' if os_version.start_with? '7.')
                    }
                  ],
                  fqdn: 'yum.internal.microstrategy.com',
                  favor: true
                },
                timezone: {
                  use_symlink: false
                },
                tz: 'America/New_York'
              })
            end
          end
        end
      end
    end
  end
end