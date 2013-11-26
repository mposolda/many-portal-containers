package org.gatein.security.identity.global;

import java.util.List;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.RootContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.management.annotations.Impact;
import org.exoplatform.management.annotations.ImpactType;
import org.exoplatform.management.annotations.Managed;
import org.exoplatform.management.annotations.ManagedDescription;
import org.exoplatform.management.annotations.ManagedName;
import org.exoplatform.management.jmx.annotations.NameTemplate;
import org.exoplatform.management.jmx.annotations.Property;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.GroupHandler;
import org.exoplatform.services.organization.Membership;
import org.exoplatform.services.organization.MembershipType;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.picocontainer.Startable;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@Managed
@ManagedDescription("Global organization service")
@NameTemplate({ @Property(key = "name", value = "globalOrganizationService"), @Property(key = "service", value = "GlobalOrganizationService") })
public class GlobalOrganizationService implements Startable {

    protected static final Log log = ExoLogger.getLogger(GlobalOrganizationService.class);

    @Managed
    @ManagedDescription("create user globally")
    @Impact(ImpactType.WRITE)
    public String createUser(@ManagedName("username") final String username,
                             @ManagedName("password") final String password,
                             @ManagedName("firstName") final String firstName,
                             @ManagedName("lastName") final String lastName,
                             @ManagedName("email") final String email) {
        return new GlobalOrgServiceTask() {

            @Override
            protected void doTask(OrganizationService organizationService) throws Exception {
                User user = organizationService.getUserHandler().createUserInstance(username);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setEmail(email);
                user.setPassword(password);
                organizationService.getUserHandler().createUser(user, true);
            }

        }.globalExecution("Creating user " + username);
    }

    @Managed
    @ManagedDescription("Save (update) user globally")
    @Impact(ImpactType.WRITE)
    public String saveUser(@ManagedName("username") final String username,
                         @ManagedName("password") final String password,
                         @ManagedName("firstName") final String firstName,
                         @ManagedName("lastName") final String lastName,
                         @ManagedName("email") final String email) {
        return new GlobalOrgServiceTask() {

            @Override
            protected void doTask(OrganizationService organizationService) throws Exception {
                User user = organizationService.getUserHandler().findUserByName(username);
                if (user == null) {
                    throw new Exception("Couldn't find user " + username);
                }

                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setEmail(email);
                user.setPassword(password);
                organizationService.getUserHandler().saveUser(user, true);
            }

        }.globalExecution("Edit user " + username);

    }

    @Managed
    @ManagedDescription("Remove user globally")
    @Impact(ImpactType.WRITE)
    public String removeUser(@ManagedName("username") final String username) {
        return new GlobalOrgServiceTask() {

            @Override
            protected void doTask(OrganizationService organizationService) throws Exception {
                organizationService.getUserHandler().removeUser(username, true);
            }

        }.globalExecution("Remove user " + username);
    }

    @Managed
    @ManagedDescription("Create group globally. Argument is groupPath (ie. /platform/toto )")
    @Impact(ImpactType.WRITE)
    public String createGroup(@ManagedName("groupPath") final String groupPath) {
        return new GlobalOrgServiceTask() {

            @Override
            protected void doTask(OrganizationService organizationService) throws Exception {
                createGroupImpl(groupPath, organizationService);
            }

            private void createGroupImpl(String groupPath, OrganizationService orgService) throws Exception {
                int lastIndex = groupPath.lastIndexOf("/");
                String parentPath = groupPath.substring(0, lastIndex);
                String groupName = groupPath.substring(lastIndex + 1);
                GroupHandler groupDAO = orgService.getGroupHandler();
                Group parentGroup = groupDAO.findGroupById(parentPath);
                if (parentGroup == null) {
                    // Create level1 Group
                    if (parentPath != null && !parentPath.isEmpty()) {
                        log.info("Parent group with ID: " + parentPath + " doesn't exist. Creating it recursively");
                        createGroupImpl(parentPath, orgService);
                    }
                    parentGroup = groupDAO.findGroupById(parentPath);
                }

                Group newGroup = groupDAO.createGroupInstance();
                newGroup.setGroupName(groupName);
                newGroup.setLabel(groupName);

                groupDAO.addChild(parentGroup, newGroup, true);
            }

        }.globalExecution("Creating group " + groupPath);
    }

    @Managed
    @ManagedDescription("Create group globally. Argument is groupPath (ie. /platform/toto )")
    @Impact(ImpactType.WRITE)
    public String removeGroup(@ManagedName("groupPath") final String groupPath) {
        return new GlobalOrgServiceTask() {

            @Override
            protected void doTask(OrganizationService organizationService) throws Exception {
                GroupHandler groupDAO = organizationService.getGroupHandler();
                Group group = groupDAO.findGroupById(groupPath);
                if (group == null) {
                    log.info("Group " + groupPath + " not found. Ignoring removal");
                    return;
                }
                groupDAO.removeGroup(group, true);
            }

        }.globalExecution("Removing group " + groupPath);
    }

    @Managed
    @ManagedDescription("Add membership globally.")
    @Impact(ImpactType.WRITE)
    public String addMembership(@ManagedName("username") final String username,
                                @ManagedName("groupPath") final String groupPath,
                                @ManagedName("membershipType") final String membershipType) {
        return new GlobalOrgServiceTask() {

            @Override
            protected void doTask(OrganizationService organizationService) throws Exception {
                User user = organizationService.getUserHandler().findUserByName(username);
                if (user == null) {
                    log.info("User " + username + " doesn't exist. Ignoring");
                    return;
                }

                Group group = organizationService.getGroupHandler().findGroupById(groupPath);
                if (group == null) {
                    log.info("Group " + groupPath + " doesn't exist. Ignoring");
                    return;
                }

                MembershipType mt = organizationService.getMembershipTypeHandler().findMembershipType(membershipType);
                if (mt == null) {
                    log.info("MembershipType " + mt + " doesn't exist. Ignoring");
                    return;
                }

                organizationService.getMembershipHandler().linkMembership(user, group, mt, true);
            }

        }.globalExecution("Add membership " + membershipType + ":" + groupPath + " to user " + username);
    }

    @Managed
    @ManagedDescription("Remove membership globally.")
    @Impact(ImpactType.WRITE)
    public String removeMembership(@ManagedName("username") final String username,
                                   @ManagedName("groupPath") final String groupPath,
                                   @ManagedName("membershipType") final String membershipType) {
        return new GlobalOrgServiceTask() {

            @Override
            protected void doTask(OrganizationService organizationService) throws Exception {
                Membership membership = organizationService.getMembershipHandler().findMembershipByUserGroupAndType(username, groupPath, membershipType);
                if (membership == null) {
                    log.info("Membership " + membershipType + ":" + groupPath + " doesn't exists on user " + username + ". Ignoring");
                    return;
                }
                organizationService.getMembershipHandler().removeMembership(membership.getId(), true);
            }

        }.globalExecution("Removing membership " + membershipType + ":" + groupPath + " to user " + username);
    }


    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }


    public static abstract class GlobalOrgServiceTask {

        public String globalExecution(String taskDescription) {
            ExoContainer currentContainer = ExoContainerContext.getCurrentContainer();

            try {
                RootContainer rootContainer = (RootContainer)ExoContainerContext.getTopContainer();
                List<PortalContainer> portalContainers = rootContainer.getComponentInstancesOfType(PortalContainer.class);

                int successCounter = 0;
                int failedCounter = 0;
                for (PortalContainer pContainer : portalContainers) {
                    OrganizationService orgService = pContainer.getComponentInstanceOfType(OrganizationService.class);

                    try {
                        ExoContainerContext.setCurrentContainer(pContainer);
                        RequestLifeCycle.begin(pContainer);
                        doTask(orgService);
                        successCounter++;
                    } catch (Exception e) {
                        log.error("Calling of task '" + taskDescription + "' failed for portalContainer '" + pContainer.getName() + "'.", e);
                        failedCounter++;
                    } finally {
                        RequestLifeCycle.end();
                    }
                }

                String output = "Task '" + taskDescription + "' done. Number of passed containers: " + successCounter + ", number of failed containers: " + failedCounter;
                if (failedCounter > 0) {
                    output = output + ", See server.log for details about errors";
                }
                return output;
            } finally {
                ExoContainerContext.setCurrentContainer(currentContainer);
            }
        }

        protected abstract void doTask(OrganizationService organizationService) throws Exception;
    }
}
