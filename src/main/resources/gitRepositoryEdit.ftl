[@ww.textfield labelKey='Repository' name='repository.git.repositoryUrl' required='true' /]
[@ww.textfield labelKey='Username' name='repository.git.username' /]

[@ww.select
    labelKey='Authentication'
    name='repository.git.authType'
    toggle='true'
    list=repository.authenticationTypes
    listKey='name'
    listValue='label']
[/@ww.select]

[@ui.bambooSection dependsOn='repository.git.authType' showOn='password']
    [#if buildConfiguration.getString('repository.git.userPassword')?has_content]
        [@ww.checkbox labelKey='Password Change' toggle='true' name='temporary.git.passwordChange' /]
        [@ui.bambooSection dependsOn='temporary.git.passwordChange' showOn='true']
            [@ww.password labelKey='Password' name="temporary.git.password" required="false" /]
        [/@ui.bambooSection]
    [#else]
        [@ww.hidden name="temporary.git.passwordChange" value="true" /]
        [@ww.password labelKey='Password' name='temporary.git.password' /]
    [/#if]
[/@ui.bambooSection]

[@ui.bambooSection dependsOn='repository.git.authType' showOn='ssh']
    [@ww.textfield labelKey='repository.git.keyFile' name='repository.git.keyFile' /]
    [#if buildConfiguration.getString('repository.git.passphrase')?has_content]
        [@ww.checkbox labelKey='repository.passphrase.change' toggle='true' name='temporary.git.passphraseChange' /]
        [@ui.bambooSection dependsOn='temporary.git.passphraseChange' showOn='true']
             [@ww.password labelKey='repository.git.passphrase' name='temporary.git.passphrase' /]
        [/@ui.bambooSection]
    [#else]
        [@ww.hidden name="temporary.git.passphraseChange" value="true" /]
        [@ww.password labelKey='repository.git.passphrase' name='temporary.git.passphrase' /]
    [/#if]
[/@ui.bambooSection]

[@ui.bambooSection titleKey='repository.advanced.option']

[@ww.checkbox labelKey='repository.advanced.option.enable' toggle='true' name='temporary.git.advanced' value='${repository.isAdvancedOptionEnabled(buildConfiguration)?string}' /]

[/@ui.bambooSection]