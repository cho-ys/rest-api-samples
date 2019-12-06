
module Smithers
  module Project
    class ConfigureCommonItems
      extend Plugin::Registrar::Registrant

      register :job_top_level, :configure_common_items, self.inspect

      attr_reader :job, :configuration_block

      def initialize(job, &block)
        @job = job
        @configuration_block = block
      end

      def configure
        job.instance_exec{
          properties do
            history_retention do
              set_to_default_30_days_or_120_builds
            end
          end

          scm do
            git do
              credentials_id '448a8313-a9ab-4093-8b8e-c42bfea4491c'
            end
          end

          publishers do
            set_build_name_and_meta_data{}
            logstash do
            end
            mailer do
              recipients 'distro@microstrategy.com'
            end
          end

          build_wrappers do
            ansicolor do
              #nothing to configure, just turn it on
            end
          end
        }
      end

    end
  end
end
