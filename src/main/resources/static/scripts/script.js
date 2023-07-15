let titleInputBox = $('#titleNoteInput');
let textNoteInputBox = $('#noteTextInput');
let taskForm = document.getElementById("tasksForm");
let taskTitle = document.getElementById("TaskTitle");

function auto_grow(element) {
    element.style.height = "auto";
    element.style.height = (element.scrollHeight) + "px";
}


// Add task to the form when the user clicks the add button
$('#addTaskButton').click(function () {
    let taskName = $('#newTaskInput').val();
    if (taskName) {
        let taskId = 'task' + ($('#tasksForm input[type="checkbox"]').length + 1);
        $('#tasksForm > .modal-body').append('<div class="form-check mt-2"><input class="form-check-input" type="checkbox" value="' + taskName + '" id="' + taskId + '" name="tasks[]"><label class="form-check-label" for="' + taskId + '">' + taskName + '</label></div>');
        $('#newTaskInput').val('');
    }

});

// Save selected tasks when the user clicks the save button
$('#saveTasksButton').click(function () {
    // let allTasks = $('#tasksForm input[type="checkbox"]').map(function () {
    //     return $(this).val();
    // }).get();
    let allTasks = {}
    $('#tasksForm input[type="checkbox"]').each((i, x) => {

        allTasks[$(x).val()] = (($(x).prop('checked')));
    });


    console.log(allTasks);

    // let fd = new FormData(taskForm);

    let data = {};
    // data['form'] = fd.entries();
    data['title'] = taskTitle.value;
    data['allTasks'] = allTasks;


    $.ajax({
        url: 'giveTask',
        type: 'POST',
        dataType: 'json',
        data: JSON.stringify(data),

        success: (dataP) => {
            console.log(dataP)
        },
        error: (jqXhr) => {
            console.log(jqXhr);
        }

    })


});

$('#saveNoteButton').click(() => {
    let data = {};

    data['title'] = titleInputBox.val();
    data['text'] = textNoteInputBox.val();


    console.log(titleInputBox.val());
    $.ajax({
        url: 'giveNote',
        type: 'POST',
        dataType: 'json',
        data: JSON.stringify(data),
        success: (dataP) => {
            console.log(dataP)
        },
        error: (jqXhr) => {
            console.log(jqXhr);
        }

    })
})

document.querySelectorAll(".taskCheckmark").forEach(elem => {
    elem.addEventListener("change", () => {
        let data = {}

        data['id'] = elem.name;
        data['checked'] = elem.checked;

        $.ajax({
            url: 'changeStatus',
            type: 'POST',
            dataType: 'json',
            data: JSON.stringify(data),
            success: (dataP) => {
                console.log(dataP)
            },
            error: (jqXhr) => {
                console.log(jqXhr);
            }

        })

    })
})

document.querySelectorAll(".delete-note-button").forEach(elem => {
    elem.addEventListener("click", () => {
        let data = {};

        data['id'] = elem.value;

        $.ajax({
            url: 'binCard',
            type: 'POST',
            dataType: 'json',
            data: JSON.stringify(data),
            success: (dataP) => {
                console.log(dataP);

                $(elem.parentElement.parentElement.parentElement).fadeOut(() => {
                    $(elem).remove();
                })

            },
            error: (jqXhr) => {
                console.log(jqXhr);
            }

        })
    })
})