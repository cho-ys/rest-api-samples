require 'shell-helper'
require 'common/version'

include ShellHelper::Shell

desc "build project in #{$WORKSPACE_SETTINGS[:paths][:project][:production][:home]}"
task :build do
  good "Puts your build command here: #{__FILE__}:#{__LINE__}, for example mvn compile or grable build"
end

desc "package project in #{$WORKSPACE_SETTINGS[:paths][:project][:production][:home]}"
task :package do
  good "Puts your pacakge command here: #{__FILE__}:#{__LINE__}"
end
