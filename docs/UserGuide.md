---
layout: page
title: User Guide
---

TaskForge is a **desktop app for managing contacts, optimized for use via a Command Line Interface** (CLI) while still having the benefits of a Graphical User Interface (GUI). If you can type fast, TaskForge can get your contact management tasks done faster than traditional GUI apps.

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure you have Java `17` or above installed in your Computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/AY2526S2-CS2103T-W09-4/tp/releases).

1. Copy the file to the folder you want to use as the _home folder_ for your TaskForge.

1. Open a command terminal, `cd` into the folder you put the jar file in, and use the `java -jar taskforge.jar` command to run the application.<br>
   A GUI similar to the below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type the command in the command box and press Enter to execute it. e.g. typing **`help`** and pressing Enter will open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all contacts.

   * `add -n John Doe -p 98765432 -e johnd@example.com` : Adds a contact named `John Doe` to TaskForge.

   * `delete 3` : Deletes the 3rd contact shown in the current list.

   * `clear` : Deletes all contacts.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<div markdown="block" class="alert alert-info">

**:information_source: Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  e.g. in `add -n NAME`, `NAME` is a parameter which can be used as `add -n John Doe`.

* Items in square brackets are optional.<br>
  e.g `-n NAME [-d TASK]` can be used as `-n John Doe -d task1` or as `-n John Doe`.

* Items with `…`​ after them can be used multiple times including zero times.<br>
  e.g. `[-d TASK]…​` can be used as ` ` (i.e. 0 times), `-d task1`, `-d task2 -d task3` etc.

* Parameters can be in any order.<br>
  e.g. if the command specifies `-n NAME -p PHONE_NUMBER`, `-pPHONE_NUMBER -nNAME` is also acceptable.

* Extraneous parameters for commands that do not take in parameters (such as `help`, `list`, `exit` and `clear`) will be ignored.<br>
  e.g. if the command specifies `help 123`, it will be interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</div>

### Viewing help : `help`

Shows a message explaining how to access the help page.

![help message](images/helpMessage.png)

Format: `help`


### Adding a person: `add`

Adds a person to TaskForge.

Format: `add -n NAME -p PHONE_NUMBER -e EMAIL [-d TASK] [-l PROJECT]…​`

<div markdown="span" class="alert alert-primary">:bulb: **Tip:**
A person can have any number of tasks and projects (including 0)
A person must have valid unique phone number and email.
</div>

Examples:
* `add -n John Doe -p 98765432 -e johnd@example.com`
* `add -n Betsy Crowe -d newTask2 -e betsycrowe@example.com -p 1234567 -d newTask1`

### Listing all persons : `list`

Shows a list of all persons in TaskForge.

Format: `list`

### Editing a person : `edit`

Edits an existing person in TaskForge.

Format: `edit INDEX [-n NAME] [-p PHONE] [-e EMAIL] [-d TASK] [-l PROJECT]…​`

* Edits the person at the specified `INDEX`. The index refers to the index number shown in the displayed person list. The index **must be a positive integer** 1, 2, 3, …​
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* When editing tasks/projects, the existing tasks/projects of the person will be removed i.e adding of tasks/projects is not cumulative.
* You can remove all the person’s tasks/projects by typing `-d`/`-l` without
    specifying any tasks/projects after it.

Examples:
*  `edit 1 -p 91234567 -e johndoe@example.com` Edits the phone number and email address of the 1st person to be `91234567` and `johndoe@example.com` respectively.
*  `edit 2 -n Betsy Crower -d` Edits the name of the 2nd person to be `Betsy Crower` and clears all existing tasks.

### Locating persons by multiple fields: `find`

Finds persons whose fields (name, phone, email, tasks, projects) match the given keywords.

Format: `find [-n NAME_KEYWORDS] [-p PHONE_KEYWORDS] [-e EMAIL_KEYWORDS] [-d TASK_KEYWORDS] [-l PROJECT_KEYWORDS]`

* The search is case-insensitive. e.g `hans` will match `Hans`
* For a specific field, persons matching at least one keyword will be returned (i.e. `OR` search).
  e.g. `find -n Hans Bo` will return `Hans Gruber`, `Bo Yang`
* When multiple fields are specified, only persons matching ALL specified fields will be returned (i.e. `AND` search).
  e.g. `find -n Alice -p 91234567` will return only persons named Alice AND with the phone number 91234567.
* Only full words will be matched e.g. `Han` will not match `Hans`.

Examples:
* `find -n John` returns `john` and `John Doe`
* `find -n alex david` returns `Alex Yeoh`, `David Li`
* `find -n Alice -p 91234567` returns any person named `Alice` whose phone number is `91234567`.
* `find -d "Task 1" -l ProjectA` returns any person who has a task containing "Task 1" and belongs to project `ProjectA`.

### Deleting a person : `delete`

Deletes the specified person from TaskForge.

Format: `delete INDEX`

* Deletes the person at the specified `INDEX`.
* The index refers to the index number shown in the displayed person list.
* The index **must be a positive integer** 1, 2, 3, …​

Examples:
* `list` followed by `delete 2` deletes the 2nd person in TaskForge.
* `find -n Betsy` followed by `delete 1` deletes the 1st person in the results of the `find` command.

### Managing projects

#### Adding a project : `project add`

Adds a new project to TaskForge into the project list.

Format: `project add PROJECT_NAME`

* Adds a new project with the specified project name.
* Project name must be alphanumeric (only letters and numbers), between 1 to 64 characters.
* Duplicate projects are not allowed.

Examples:
* `project add WebApp` adds a new project named `WebApp`.
* `project add MobileApp` adds a new project named `MobileApp`.

#### Viewing all projects : `project list`

Displays all projects currently available in TaskForge.

Format: `project list`

Example:
* `project list` returns a list of all existing project

#### Deleting a project : `project delete`

Deletes a project by its index in the displayed project list.

Format: `project delete INDEX`

* Deletes the project at the specified `INDEX`.
* The index refers to the index number shown by `project list`.
* The index **must be a positive integer** `1, 2, 3, ...`

Example:
* `project list` followed by `project delete 2` deletes the 2nd project in the list.

#### Finding projects by name : `project find`

Finds projects whose names contain any of the given keywords.

Format: `project find KEYWORD [MORE_KEYWORDS]`

* The search is case-insensitive. e.g. `alpha` will match `Alpha`
* The search is performed on project names only.
* Projects matching at least one keyword will be shown (i.e. `OR` search).
* The result is shown as plain text in the result display. It does not filter or change the person list.

Examples:
* `project find alpha`
* `project find web mobile`

#### Assigning a project : `project assign`

Assigns a project to a person

Format: `project assign PERSON_INDEX -n PROJECT_NAME`

* Assigns project(s) from `project list` to a person.
* Project should exist in `project list` before being assigned to a person.
* `PERSON_INDEX` refers to the person index that's displayed on `list`
* `PERSON_INDEX` **must be a positive integer** `1, 2, 3, ...`
* The same project cannot be assigned twice to the same person (no duplicates)
* To assign multiple projects in one command, repeat the `-n` prefix.

Example:
* `project assign 1 -n WebApp` checks whether the project named WebApp exists, then assigns the project to the 1st person in the `list`
* `project assign 2 -n WebApp -n MobileApp` assigns multiple projects to the 2nd person in the `list`

#### Unassigning a project : `project unassign`

Unassigns a project from a person

Format: `project unassign PERSON_INDEX -i PROJECT_INDEX`

* Unassigns project(s) from the person at the specified `INDEX`.
* `PROJECT_INDEX` refers to the project numbering shown for that person in the app.
* `PROJECT_INDEX` does NOT refer to the project index that's displayed on `project list`.
* `PERSON_INDEX` refers to the person index that's displayed on `list`
* Both person `PERSON_INDEX` and `PROJECT_INDEX` **must be positive integers** `1, 2, 3, ...`
* To unassign multiple projects in one command, repeat the `-i` prefix.

Examples:
* `project unassign 1 -i 2` unassigns the 2nd project from the 1st person in the `list`
* `project unassign 3 -i 1 -i 4` unassigns the 1st and 4th projects from the 3rd person in the `list`

### Managing tasks

#### Adding a task to a project : `task add`

Adds task(s) to a project in the project list.

Format: `task add PROJECT_INDEX -n TASK_NAME`

* Adds new task(s) with the specified `TASK_NAME` to the project at the specified `PROJECT_INDEX`.
* The project index refers to the index number shown by `project list`.
* The project index **must be a positive integer** `1, 2, 3, ...`
* Task name must be alphanumeric (only letters, numbers and spaces), between 1 to 64 characters.
* Duplicate tasks within the same project are not allowed.
* To add multiple tasks in one command, repeat the `-n` prefix.

Examples:
* `task add 1 -n Write documentation`
* `task add 2 -n Design UI -n Implement backend`

#### Deleting a task from a project : `task delete`

Deletes a task from a project in the project list.

Format: `task delete PROJECT_INDEX -i TASK_INDEX`

* Deletes task(s) from the project at the specified `PROJECT_INDEX`.
* `TASK_INDEX` refers to the task numbering shown for that project in the app.
* Both `PROJECT_INDEX` and `TASK_INDEX` **must be positive integers** `1, 2, 3, ...`
* To delete multiple tasks from the same project in one command, repeat the `-i` prefix.

Examples:
* `task delete 1 -i 2` deletes the 2nd task from the 1st project
* `task delete 2 -i 1 -i 3` deletes the 1st and 3rd tasks from the 2nd project

#### Editing a task in a project : `task edit`

Edits the name of an existing task in a project.

Format: `task edit PROJECT_NAME -i TASK_INDEX_FROM_PROJECT -n NEW_TASK_NAME`

* Edits the task at `TASK_INDEX_FROM_PROJECT` in the project named `PROJECT_NAME`.
* `TASK_INDEX_FROM_PROJECT` refers to the task numbering shown for that project in the app.
* `TASK_INDEX_FROM_PROJECT` **must be a positive integer** `1, 2, 3, ...`
* `NEW_TASK_NAME` must be alphanumeric (only letters, numbers and spaces), between 1 to 64 characters.
* Duplicate task names in the same project are not allowed.
* If the edited task is assigned to any person, TaskForge preserves those assignments by reassigning the renamed task.

Examples:
* `task edit Alpha -i 1 -n Prepare sprint report`
* `task edit Project X -i 2 -n Finalize API contract`

#### Listing all tasks in a project : `task list`

Lists all tasks that belong to the specified project.

Format: `task list -n PROJECT_NAME`

* Lists task(s) from the project named `PROJECT_NAME`.
* `PROJECT_NAME` must match an existing project in TaskForge.
* Project name matching is case-insensitive.

Examples:
* `task list -n Alpha`
* `task list -n Project X`

#### Finding tasks by keyword : `task find`

Finds tasks whose names contain any of the given keywords across all projects.

Format: `task find KEYWORD [MORE_KEYWORDS]`

* The search is case-insensitive. e.g. `report` will match `Write report`.
* The search checks all task lists from every project.
* A matching result is shown as `taskName - projectName`.
* Tasks matching at least one keyword will be shown (i.e. `OR` search).

Examples:
* `task find report`
* `task find bug ui`

#### Assigning a task : `task assign`

Assigns one or more tasks to a person.

Format: `task assign INDEX -n TASK_NAME`

* Adds task(s) to the person at the specified `INDEX`.
* The person index refers to the index number shown in the displayed person list.
* The person index **must be a positive integer** `1, 2, 3, ...`
* To assign multiple tasks in one command, repeat the `-n` prefix.

Examples:
* `task assign 1 -n Write report`
* `task assign 2 -n Prepare slides -n Rehearse demo`

#### Unassigning a task : `task unassign`

Unassigns one or more tasks from a person by task index.

Format: `task unassign INDEX -i TASK_INDEX`

* Deletes task(s) from the person at the specified `INDEX`.
* `TASK_INDEX` refers to the task numbering shown for that person in the app.
* Both person `INDEX` and `TASK_INDEX` **must be positive integers** `1, 2, 3, ...`
* To unassign multiple tasks in one command, repeat the `-i` prefix.

Examples:
* `task unassign 1 -i 2`
* `task unassign 3 -i 1 -i 4`

#### Viewing all tasks of a person : `task view`

Displays all tasks assigned to a person.

Format: `task view INDEX`

* Shows all tasks assigned to the person at the specified `INDEX`.
* The person index refers to the index number shown in the displayed person list.
* The index **must be a positive integer** `1, 2, 3, ...`
* If the person has no tasks, a message will be shown.

Examples:
* `task view 1`
* `task view 2`

#### Marking a task as done : `task mark`

Marks a task as done for an existing person in the address book.

Format: `task mark PERSON_INDEX TASK_INDEX`

* Marks the task identified by `TASK_INDEX` as done for the person identified by `PERSON_INDEX`.
* `PERSON_INDEX` refers to the person index in the displayed person list.
* `TASK_INDEX` refers to the task index in the person's task list (visible via `task view`).
* Both indices **must be positive integers** `1, 2, 3, ...`

Example:
* `task mark 1 1` marks the 1st task of the 1st person as done.

#### Unmarking a task as done : `task unmark`

Unmarks a task as done for an existing person in the address book.

Format: `task unmark PERSON_INDEX TASK_INDEX`

* Unmarks the task identified by `TASK_INDEX` as done for the person identified by `PERSON_INDEX`.
* `PERSON_INDEX` refers to the person index in the displayed person list.
* `TASK_INDEX` refers to the task index in the person's task list (visible via `task view`).
* Both indices **must be positive integers** `1, 2, 3, ...`

Example:
* `task unmark 1 1` marks the 1st task of the 1st person as not done.

### Clearing all entries : `clear`

Clears all entries from TaskForge.

Format: `clear`

### Exiting the program : `exit`

Exits the program.

Format: `exit`

### Saving the data

TaskForge data are saved in the hard disk automatically after any command that changes the data. There is no need to save manually.

### Editing the data file

TaskForge data are saved automatically as a JSON file `[JAR file location]/data/addressbook.json`. Advanced users are welcome to update data directly by editing that data file.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
If your changes to the data file makes its format invalid, TaskForge will discard all data and start with an empty data file at the next run. Hence, it is recommended to take a backup of the file before editing it.<br>
Furthermore, certain edits can cause TaskForge to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</div>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another Computer?<br>
**A**: Install the app in the other computer and overwrite the empty data file it creates with the file that contains the data of your previous TaskForge home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action | Format, Examples
--------|------------------
**Add Person** | `add -n NAME -p PHONE_NUMBER -e EMAIL [-l PROJECT_NAME] [-d TASK_NAME]…​` <br> e.g., `add -n James Ho -p 22224444 -e jamesho@example.com -l ProjectX -d TaskY`
**Add Project** | `project add PROJECT_NAME`<br> e.g., `project add WebApp`
**Add Task** | `task add PROJECT_INDEX -n TASK_NAME`<br> e.g., `task add 1 -n Write report`
**Delete Task** | `task delete PROJECT_INDEX -i TASK_INDEX`<br> e.g., `task delete 1 -i 2`
**Edit Task** | `task edit PROJECT_NAME -i TASK_INDEX_FROM_PROJECT -n NEW_TASK_NAME`<br> e.g., `task edit Alpha -i 1 -n Prepare sprint report`
**List Tasks by Project** | `task list -n PROJECT_NAME`<br> e.g., `task list -n Alpha`
**Find Tasks** | `task find KEYWORD [MORE_KEYWORDS]`<br> e.g., `task find report bug`
**Assign Task** | `task assign INDEX -n TASK_NAME`<br> e.g., `task assign 1 -n Draft proposal`
**Assign Project** | `project assign INDEX -n PROJECT_NAME`<br> e.g., `project assign 1 -n WebApp`
**Clear** | `clear`
**Delete** | `delete INDEX`<br> e.g., `delete 3`
**Delete Project** | `project delete INDEX`<br> e.g., `project delete 1`
**Find Project** | `project find KEYWORD [MORE_KEYWORDS]`<br> e.g., `project find Alpha Web`
**View Tasks** | `task view INDEX`<br> e.g.,`task view 1`
**Mark Task** | `task mark PERSON_INDEX TASK_INDEX`<br> e.g., `task mark 1 1`
**Unmark Task** | `task unmark PERSON_INDEX TASK_INDEX`<br> e.g., `task unmark 1 1`
**Edit Person** | `edit Index [-n NAME] [-p PHONE_NUMBER] [-e EMAIL] [-l PROJECT_NAME] [-d TASK_NAME]…​` <br> e.g., `edit Index -n James Ho -p 22224444 -e jamesho@example.com -l ProjectX -d TaskY`
**Find Person** | `find KEYWORD [MORE_KEYWORDS]`<br> e.g., `find James Jake`
**List Person** | `list`
**Unassign Task** | `task unassign INDEX -i TASK_INDEX`<br> e.g., `task unassign 2 -i 1`
**Edit Person** | `edit INDEX [-n NAME] [-p PHONE_NUMBER] [-e EMAIL] [-l PROJECT_NAME] [-d TASK_NAME]…​` <br> e.g., `edit INDEX -n James Ho -p 22224444 -e jamesho@example.com -l ProjectX -d TaskY`
**Unassign Project** | `project unassign INDEX -i PROJECT_INDEX`<br> e.g., `project unassign 2 -i 1`
**View Projects** | `project list`
**Help** | `help`
