[@ww.textfield labelKey='Repository URL'
               name='repository.git.repositoryUrl'
               description= 'The location of Git repository to clone (e.g. git@github.com:andypols/git-bamboo-plugin.git)'
               required='true' /]

[@ww.select
    labelKey='Authentication Type'
    name='repository.git.authType'
    toggle='true'
    list=repository.authenticationTypes
    listKey='name'
    listValue='label']
[/@ww.select]

[@ui.bambooSection dependsOn='repository.git.authType' showOn='password']
    <p>
        Not implemented Yet
    </p>
[/@ui.bambooSection]

[@ui.bambooSection dependsOn='repository.git.authType' showOn='ssh']
    [@ww.textfield labelKey='Private Key'
                   name='repository.git.keyFile'
                   description='Enter the absolute path of the private key' /]
[/@ui.bambooSection]