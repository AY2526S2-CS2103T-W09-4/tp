package seedu.taskforge.logic.commands.person;

import static java.util.Objects.requireNonNull;
import static seedu.taskforge.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.taskforge.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.taskforge.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.taskforge.logic.parser.CliSyntax.PREFIX_PROJECT_TITLE;
import static seedu.taskforge.logic.parser.CliSyntax.PREFIX_TASK;
import static seedu.taskforge.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.taskforge.commons.core.index.Index;
import seedu.taskforge.commons.util.CollectionUtil;
import seedu.taskforge.commons.util.ToStringBuilder;
import seedu.taskforge.logic.Messages;
import seedu.taskforge.logic.commands.Command;
import seedu.taskforge.logic.commands.CommandResult;
import seedu.taskforge.logic.commands.exceptions.CommandException;
import seedu.taskforge.model.Model;
import seedu.taskforge.model.person.Email;
import seedu.taskforge.model.person.Name;
import seedu.taskforge.model.person.Person;
import seedu.taskforge.model.person.PersonProject;
import seedu.taskforge.model.person.PersonTask;
import seedu.taskforge.model.person.Phone;
import seedu.taskforge.model.project.Project;
import seedu.taskforge.model.task.Task;

/**
 * Edits the details of an existing person in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the person identified "
            + "by the index number used in the displayed person list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_PROJECT_TITLE + "PROJECT] "
            + "[" + PREFIX_TASK + "TASK]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited Person: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_PERSON = "Another person with the "
            + "same name, phone, or email already exists in taskforge";
    public static final String MESSAGE_TASK_NOT_IN_ASSIGNED_PROJECTS =
            "Task to assign does not exist in any assigned project.";
    public static final String MESSAGE_DUPLICATE_TASK = "This task already exists for this person!";

    private final Index index;
    private final EditPersonDescriptor editPersonDescriptor;

    /**
     * @param index of the person in the filtered person list to edit
     * @param editPersonDescriptor details to edit the person with
     */
    public EditCommand(Index index, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(index);
        requireNonNull(editPersonDescriptor);

        this.index = index;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        Person editedPerson = createEditedPerson(personToEdit, editPersonDescriptor, model);

        if (!personToEdit.isSamePerson(editedPerson) && model.hasPerson(editedPerson)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson)));
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Person createEditedPerson(Person personToEdit, EditPersonDescriptor editPersonDescriptor,
            Model model) throws CommandException {
        assert personToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        Phone updatedPhone = editPersonDescriptor.getPhone().orElse(personToEdit.getPhone());
        Email updatedEmail = editPersonDescriptor.getEmail().orElse(personToEdit.getEmail());
        List<PersonProject> updatedPersonProjects;

        if (editPersonDescriptor.getProjects().isPresent()) {
            List<Project> projectsToAssign = editPersonDescriptor.getProjects().get();
            updatedPersonProjects = new ArrayList<>();
            List<Project> globalProjectList = new ArrayList<>(model.getProjectList());

            for (Project project : projectsToAssign) {
                int projectIndex = globalProjectList.indexOf(project);
                if (projectIndex == -1) {
                    throw new CommandException("The project to assign does not exist in the address book");
                }
                updatedPersonProjects.add(new PersonProject(projectIndex));
            }
        } else {
            updatedPersonProjects = personToEdit.getProjects();
        }

        List<PersonTask> updatedTasks = editPersonDescriptor.getTasks().isPresent()
            ? resolvePersonTasks(editPersonDescriptor.getTasks().get(), updatedPersonProjects,
                new ArrayList<>(model.getProjectList()))
            : personToEdit.getTasks();

        return new Person(updatedName, updatedPhone, updatedEmail,
                updatedPersonProjects, updatedTasks);
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
        if (!(other instanceof EditCommand)) {
            return false;
        }

        EditCommand otherEditCommand = (EditCommand) other;
        return index.equals(otherEditCommand.index)
                && editPersonDescriptor.equals(otherEditCommand.editPersonDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editPersonDescriptor", editPersonDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private List<Project> projects;
        private List<Task> tasks;

        public EditPersonDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tasks} is used internally.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setProjects(toCopy.projects);
            setTasks(toCopy.tasks);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, projects, tasks);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        /**
         * Sets {@code tasks} to this object's {@code tasks}.
         * A defensive copy of {@code tasks} is used internally.
         */
        public void setTasks(List<Task> tasks) {
            this.tasks = (tasks != null) ? new ArrayList<>(tasks) : null;
        }

        /**
         * Returns an unmodifiable task set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tasks} is null.
         */
        public Optional<List<Task>> getTasks() {
            return (tasks != null) ? Optional.of(Collections.unmodifiableList(tasks)) : Optional.empty();
        }

        /**
         * Sets {@code tasks} to this object's {@code tasks}.
         * A defensive copy of {@code tasks} is used internally.
         */
        public void setProjects(List<Project> projects) {
            this.projects = (projects != null) ? new ArrayList<>(projects) : null;
        }

        /**
         * Returns an unmodifiable task set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tasks} is null.
         */
        public Optional<List<Project>> getProjects() {
            return (projects != null) ? Optional.of(Collections.unmodifiableList(projects)) : Optional.empty();
        }



        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor)) {
                return false;
            }

            EditPersonDescriptor otherEditPersonDescriptor = (EditPersonDescriptor) other;
            return Objects.equals(name, otherEditPersonDescriptor.name)
                    && Objects.equals(phone, otherEditPersonDescriptor.phone)
                    && Objects.equals(email, otherEditPersonDescriptor.email)
                    && Objects.equals(projects, otherEditPersonDescriptor.projects)
                    && Objects.equals(tasks, otherEditPersonDescriptor.tasks);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .add("email", email)
                    .add("projects", projects)
                    .add("tasks", tasks)
                    .toString();
        }
    }
}
