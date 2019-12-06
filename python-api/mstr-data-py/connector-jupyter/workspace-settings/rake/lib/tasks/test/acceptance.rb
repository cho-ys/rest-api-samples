
desc "test project in #{$WORKSPACE_SETTINGS[:paths][:project][:production][:home]}"
task :test do
  good "Puts your test command here, for example mvn test or grable test: #{__FILE__}:#{__LINE__}"
end
