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
String.prototype.startsWith = function(str) {return (this.match("^"+str)==str)}

function populateGitHubWebUrl(textForm) {
    if (textForm.value.startsWith("git@github.com:")) {
        var newString = textForm.value.replace('git@github.com:', 'https://github.com/').replace('.git', '');

        //
        // the field is called different things on different pages
        //
        var repoWebUrlField = document.getElementById('updateBuildRepository_repository_common_webRepositoryUrl');
        if(repoWebUrlField == null) {
            repoWebUrlField = document.getElementById('createBuildRepository_repository_common_webRepositoryUrl');
        }

        repoWebUrlField.value = newString;
    }
}
//-->
</SCRIPT>