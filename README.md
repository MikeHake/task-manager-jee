# Introduction

This is a reference Java EE 7 application I created for the purpose of exploring the patterns and 
best practices of providing 'enterprise' type services using Java EE 7.

Below is a brief description of how to set up and run the application, and I have also written a more
detailed article on my blog here: TODO: Insert link!!!

# What the application does

This provides a JSON based REST API that could serve as the back end for a task list type application. The application
uses only Java EE 7 (no 3rd party libraries at all), so the API is provided using the JAX-RS. 

* The application uses JEE 7 declarative and programmatic security and users must have one or more of the following roles: USER, ADMIN
* Users with ADMIN role are allowed to create new projects
* Users with ADMIN role can assign users, or members, to a project
* Users with ADMIN role can designate users to be project admin on a given project
* A user who has been designated a project admin for a given project can also assign/remove other users from a project
* Standard users can see only the projects to which they are members
* Standard users can create tasks on projects to which they are members
* There is no user management in this application. That is handled externally and we rely on the Java EE container to tell us who the calling user is and what roles they have. All this application needs to persist about users is a simple table containing just the user ID's, so they can be associated with projects and tasks. 

# To run the application on WildFly

In theory you should be able to run this on any Java EE 7 compliant container. However the only one I have used for my development and testing is WildFly 8.2.0. Here are some instructions for running on WildFly.

* Download WildFly 8.2.0 here: http://wildfly.org/downloads/ 
  * Take a quick look at the WildFly getting started guide: https://docs.jboss.org/author/display/WFLY8/Getting+Started+Guide
  * Use this command to start WildFly: `$JBOSS_HOME/bin/standalone.sh`
    * Verify it started by pointing browser at: http://localhost:8080/
  * Use this command to stop WildFly: `$JBOSS_HOME/bin/jboss-cli.sh --connect --command=:shutdown`
* Build the `task-manager-jee.war` file using maven command: `mvn clean install`
* Copy the `task-manager-jee.war` file to the WildFly `standalone/deployments` folder
* At this point the application should deploy when WildFly starts. However you wont be able to do anything with the API yet because you have not configured any users. So next we will add a few users to the WildFly built in `ApplicationRealm`. See the following links to WildFly documentation to understand the built in `ApplicationRealm` and the `add-user` utility:
  * https://docs.jboss.org/author/display/WFLY8/Security+Realms 
  * https://docs.jboss.org/author/display/WFLY8/add-user+utility
* Next, use the WildFly `add-user` utility to add the following users. These will be needed by the automated tests that we will get to shortly:
  * Username: admin, Password:secret, Groups: ADMIN,USER 
  * Username: projectAdmin1, Password:secret, Groups: USER 
  * Username: projectAdmin2, Password:secret, Groups: USER
  * Username: user1, Password:secret, Groups: USER 
  * Username: user2, Password:secret, Groups: USER
* Note: The above step of setting up the users is a bit of a pain and might be the most time consuming part of the setup. In a real enterprise application deployment we would be integrating with some other system and would not need to fool around with the built in WildFly ApplicationRealm.
* At this point you should be able to start WildFly and `task-manager-jee.war` should deploy. You can now run a suite of automated tests against the API to verify its operation. You can clone and find instructions for those tests here: https://github.com/MikeHake/task-manager-api-tests    


# API Doc

## Base URL (assuming localhost)

    http://localhost:8080/task-manager-jee/v1

## List all projects

    GET /projects

## Create a project

    POST /projects

## Get single project details

    GET /projects/:projectName
    
## Edit a project

    PUT /projects/:projectName
    
## Delete single project

    DELETE /projects/:projectName

## Add a user to a project

    PUT /projects/:projectName/members/:userName
    
## Remove user from a project

    DELETE /projects/:projectName/members/:userName

## List all users associated with a project

    DELETE /projects/:projectName/members

## Add a project admin to a project

    PUT /projects/:projectName/admins/:userName
    
## Remove a project admin from a project

    DELETE /projects/:projectName/admins/:userName

## Get list of tasks associated with a project

    GET /projects/:projectName/tasks
   
## Add a task to a project

    POST /projects/:projectName/tasks
    
## Get a single task details

    GET /projects/:projectName/tasks/:taskId

## List all users known to the application

    GET /users

## List a single user details

    GET /users/:userName


