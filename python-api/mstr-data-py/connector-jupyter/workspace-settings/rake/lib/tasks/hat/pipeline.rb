require 'dependencies_db'

Rake::Task[:upload].clear_prerequisites

desc "deploy project in #{$WORKSPACE_SETTINGS[:paths][:project][:production][:home]}"
task :deploy, :version do |t, args|
  good "Puts deployment code here: #{__FILE__}:#{__LINE__}"
end

task :update_dep_db_from_file do
  client = DependenciesDB.new
  client.update_dependencies_from_file('json')
  client.update_dependencies_table
end
