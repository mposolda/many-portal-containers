How to manage users across all portal containers
================================================
When you have setup with many portal containers, you usually want your users to be isolated (ie. User "joe" created in container
"sampleportal4" doesn't exists in other portal containers and he don't have access to other portal containers). Fact is that if user
registers through GateIn UI, he is available only in the container, where he registers and other portal containers can't see him.

However you may want some users (usually admin users) to have access to all portal containers. Technically it's not possible to
 share users/groups/memberships across more portal containers, but it's defacto doable if you have same "copy" of particular user/group/membership
 in each portal container. There are several ways how to achieve it:

1) Use organization-configuration.xml - users/groups/memberships which are in this config file are checked during startup of OrganizationService
 and created if they aren't. This change is done in each portal container during it's boot. From GateIn 3.6.4.Final it's possible to
 update users via organization-configuration.xml (you need to switch option "updateUsers" to achieve it - TODO: more info...)
 Limitation is that you can't remove users/groups/memberships through organization-configuration.xml and also another thing is that
 restart is required if you want to perform some change.

2) Configure your organization with LDAP - For example you can add your "shared" users/groups to LDAP server and keep rest of the users in DB.
  Since all portal containers will have same instance of LDAP, they will all see same "shared" admin users.

3) Use the global-org-service-controller tool. You need to do these steps:

3.a) Run "mvn clean install" in this project

3.b) Then run "cp target/global-org-service-controller-*.jar GATEIN_HOME/modules/org/gatein/lib/main/

3.c) Open GATEIN_HOME/modules/org/gatein/lib/main/module.xml and configured the JAR from previous step. It will look similarly to this:
<resource-root path="global-org-service-controller-0.1-SNAPSHOT.jar"/>

3.d) Run GateIn. Then run jconsole and look for mbean "exo:service=GlobalOrganizationService,name=globalOrganizationService" . Here you can
execute various operations via JMX. Arguments of all operations are Strings, so it's not problem to run stuff from jconsole. All operations
executed here will be propagated to all portal containers





