require "deep_merge"
require "vagrant/project/environment/base"

module Vagrant
  module Project
    module Environment
      module ESXI
        class Default < Vagrant::Project::Environment::Base
          register :environment, :esxi, self.inspect

          def configure_provider(machine, &block)
            machine.provider.set_defaults{|vbox|
              #example
              #vbox.name = vagrant_machine.name
            }

            machine.provider.configuration.with{
              host          "10.23.45.101"
              datastore     "datastore1"
              user          "admin"
              password      "9@55w046!"
              ssh_key_path  "~/.ssh/esxi.key"
            }
            block.call()
          end

          def configure_provisioner(machine, &block)
            Berkshelf::Berksfile.preposition_berksfile(File.expand_path('esxi_default.berks', File.dirname(__FILE__)))

            machine.provisioner.set_defaults do |chef|
              chef.file_cache_path = '/var/chef/cache/artifacts'

              chef.add_recipe "chef_commons"
              chef.add_recipe "dev_commons::hosts"
              chef.add_recipe "yum_mirror::repo"
              chef.add_recipe "timezone-ii"
              chef.add_recipe "ntp"
            end

            machine.provisioner.configure do |chef|
              block.call()
            end

            machine.provisioner.configure do |chef|
              chef.json.deep_merge!({
                data_bag_secret: 'Z91%gDUDF5!jz0UE3ZC$9VR!o892',
                data_bags: {
                  servers: {
                    git: {
                      fqdn: 'yum.internal.microstrategy.com',
                      ip: '10.242.102.113'
                    }
                  }
                },
                yum_mirror: {
                  mirrors: [
                    {
                      name: 'microstrategy-rhel-6-extra',
                      url:  'https://yum.internal.microstrategy.com/extra'
                    }
                  ],
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