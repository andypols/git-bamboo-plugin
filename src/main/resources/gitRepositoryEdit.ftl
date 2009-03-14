[@ww.textfield labelKey='Repository' name='repository.git.repositoryUrl' required='true' /]

[@ww.select
    labelKey='Authentication'
    name='repository.git.authType'
    toggle='true'
    list=repository.authenticationTypes
    listKey='name'
    listValue='label']
[/@ww.select]

[@ui.bambooSection dependsOn='repository.git.authType' showOn='password']
    PASSWORD
[/@ui.bambooSection]

[@ui.bambooSection dependsOn='repository.git.authType' showOn='ssh']
    SSH
[/@ui.bambooSection]

[@ui.bambooSection dependsOn='repository.git.authType' showOn='ssl-client-certificate']
    SSL
[/@ui.bambooSection]