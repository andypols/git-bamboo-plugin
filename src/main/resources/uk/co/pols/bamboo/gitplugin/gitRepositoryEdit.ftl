[@ww.textfield labelKey='Repository URL'
               name='repository.github.repositoryUrl'
               description='The location of Git repository to clone (e.g. git@github.com:andypols/git-bamboo-plugin.git)'
               required='true'
               onblur='populateGitHubWebUrl(this);' /]

[@ww.textfield labelKey='Branch'
               name='repository.github.branch'
               description= 'The repository branch to build'
               required='true' /]

<SCRIPT TYPE="text/javascript">
<!--
function populateGitHubWebUrl(textForm) {
    var newString = textForm.value.replace('git@github.com:', 'https://github.com/').replace('.git', '');
    document.getElementById('createBuildRepository_repository_common_webRepositoryUrl').value = newString;
}
//-->
</SCRIPT>