package seedu.taskforge.logic.commands.person;

import static java.util.Objects.requireNonNull;
import static seedu.taskforge.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.taskforge.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.taskforge.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.taskforge.logic.parser.CliSyntax.PREFIX_PROJECT_TITLE;
import static seedu.taskforge.logic.parser.CliSyntax.PREFIX_TASK;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.taskforge.commons.util.ToStringBuilder;
import seedu.taskforge.logic.Messages;
import seedu.taskforge.logic.commands.Command;
import seedu.taskforge.logic.commands.CommandResult;
import seedu.taskforge.logic.commands.exceptions.CommandException;
import seedu.taskforge.model.Model;
import seedu.taskforge.model.person.Person;
import seedu.taskforge.model.person.PersonProject;
import seedu.taskforge.model.person.PersonTask;
import seedu.taskforge.model.project.Project;
import seedu.taskforge.model.task.Task;

/**
 * Adds a person to the address book.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a person to the address book. "
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + PREFIX_PHONE + "PHONE "
            + PREFIX_EMAIL + "EMAIL "
            + "[" + PREFIX_PROJECT_TITLE + "PROJECT] "
            + "[" + PREFIX_TASK + "TASK]...\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "John Doe "
            + PREFIX_PHONE + "98765432 "
            + PREFIX_EMAIL + "johnd@example.com "
            + PREFIX_PROJECT_TITLE + "Mobile app "
            + PREFIX_TASK + "refactor code";

    public static final String MESSAGE_SUCCESS = "New person added: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "Another person with the "
            + "same name, phone, or email already exists in taskforge";
    public static final String MESSAGE_TASK_NOT_IN_ASSIGNED_PROJECTS =
            "Task to assign does not exist in any assigned project.";
    public static final String MESSAGE_DUPLICATE_TASK = "This task already exists for this person!";

    private final Person toAdd;
    private final List<Project> projectsToAssign;
    private final List<Task> tasksToAssign;

    /**
     * Creates an AddCommand to add the specified {@code Person}.
     */
    public AddCommand(Person person, List<Project> projectsToAssign, List<Task> tasksToAssign) {
        requireNonNull(person);
        requireNonNull(projectsToAssign);
        requireNonNull(tasksToAssign);
        toAdd = person;
        this.projectsToAssign = new ArrayList<>(projectsToAssign);
        this.tasksToAssign = new ArrayList<>(tasksToAssign);
    }

    /**
     * Creates an AddCommand to add the specified {@code Person} with no project/task assignments.
     */
    public AddCommand(Person person) {
        this(person, new ArrayList<>(), new ArrayList<>());
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasPerson(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        // Convert Projects to PersonProjects before assigning to a person
        List<PersonProject> personProjectsToAssign = new ArrayList<>();
        if (!projectsToAssign.isEmpty()) {
            List<Project> globalProjectList = new ArrayList<>(model.getProjectList());
            for (Project project : projectsToAssign) {
                int projectIndex = globalProjectList.indexOf(project);
                if (projectIndex == -1) {
                    throw new CommandException("The project to assign does not exist in the address book.");
                }
                personProjectsToAssign.add(new PersonProject(projectIndex));
            }
        }

        List<PersonTask> personTasksToAssign = resolvePersonTasks(tasksToAssign, personProjectsToAssign,
                globalProjectListOrEmpty(model));

        Person personWithProjects = new Person(
                toAdd.getName(),
                toAdd.getPhone(),
                toAdd.getEmail(),
                personProjectsToAssign,
                personTasksToAssign
        );

        model.addPerson(personWithProjects);
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(personWithProjects)));
    }

    private static List<Project> globalProjectListOrEmpty(Model model) {
        return new ArrayList<>(model.getProjectList());
    }

    private static List<PersonTask> resolvePersonTasks(List<Task> inputTasks, List<PersonProject> assignedProjects,
                                                       List<Project> allProjects) throws CommandException {
        List<PersonTask> resolved = new ArrayList<>();
        Set<String> uniqueTaskRefs = new HashSet<>();

        for (Task task : inputTasks) {
            PersonTask personTask = resolveSingleTask(task, assignedProjects, allProjects);
            String key = personTask.getProjectIndex() + ":" + personTask.getTaskIndex();
            if (!uniqueTaskRefs.add(key)) {
                throw new CommandException(MESSAGE_DUPLICATE_TASK);
            }
            resolved.add(personTask);
        }

        return resolved;
    }

    private static PersonTask resolveSingleTask(Task task, List<PersonProject> assignedProjects,
                                                List<Project> allProjects) throws CommandException {
        for (PersonProject personProject : assignedProjects) {
            int projectIndex = personProject.getProjectIndex();
            if (projectIndex < 0 || projectIndex >= allProjects.size()) {
                continue;
            }

            Project project = allProjects.get(projectIndex);
            if (task.getProjectTitle() != null && !project.title.equals(task.getProjectTitle())) {
                continue;
            }

            int taskIndex = project.getTasks().indexOf(task);
            if (taskIndex >= 0) {
                return new PersonTask(projectIndex, taskIndex);
            }
        }

        throw new CommandException(MESSAGE_TASK_NOT_IN_ASSIGNED_PROJECTS);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddCommand)) {
            return false;
        }

        AddCommand otherAddCommand = (AddCommand) other;
        return toAdd.equals(otherAddCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
