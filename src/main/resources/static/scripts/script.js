let titleInputBox = $('#titleNoteInput');
let textNoteInputBox = $('#noteTextInput');
let taskForm = document.getElementById("tasksForm");
let taskTitle = document.getElementById("TaskTitle");
let notesContainer = document.getElementById("notesContainer");

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
        url: 'giveTask', type: 'POST', dataType: 'json', data: JSON.stringify(data),

        success: (dataP) => {
            console.log(dataP)
        }, error: (jqXhr) => {
            console.log(jqXhr);
        }

    })


});

//TODO: da se pojavuva odma posle dodavanje
$('#saveNoteButton').click(() => {
    let data = {};

    data['title'] = titleInputBox.val();
    data['text'] = textNoteInputBox.val();


    console.log(titleInputBox.val());

    $('#saveNoteButton').html(`<div class="spinner-border spinner-border-sm" role="status">
                            <span class="visually-hidden">Loading...</span>
                        </div>`).prop("disabled",true);


    $.ajax({
        url: 'giveNote', type: 'POST', dataType: 'json', data: JSON.stringify(data), success: (dataP) => {
            console.log(dataP)
            let noteData = dataP[0];

            let template = makeNote(noteData['title'], noteData['text'], noteData['id']);

            notesContainer.insertAdjacentHTML('afterbegin', template);

            $('#saveNoteButton').html('Save changes').prop("disabled",false);

            titleInputBox.val("");
            textNoteInputBox.val("") ;


        }, error: (jqXhr) => {
            console.log(jqXhr);
        }

    })
})

document.querySelectorAll(".taskCheckmark").forEach(elem => {
    elem.addEventListener("change", () => {
        let data = {}

        data['id'] = elem.name;
        data['checked'] = elem.checked;

        if (elem.checked) elem.nextElementSibling.classList.add("strike"); else elem.nextElementSibling.classList.remove("strike");


        $.ajax({
            url: 'changeStatus', type: 'POST', dataType: 'json', data: JSON.stringify(data), success: (dataP) => {
                console.log(dataP)
            }, error: (jqXhr) => {
                console.log(jqXhr);
            }

        })

    })
})

document.querySelectorAll(".delete-note-button").forEach(elem => {
    elem.addEventListener("click", () => {
        let data = {};

        data['id'] = elem.value;
        $(elem.parentElement.parentElement.parentElement).fadeOut(200, () => {

            $.ajax({
                url: 'binCard', type: 'POST', dataType: 'json', data: JSON.stringify(data), success: (dataP) => {
                    console.log(dataP);

                    $(elem).remove();

                }, error: (jqXhr) => {
                    console.log(jqXhr);
                }

            })
        })

    })
})

function makeNote(title, text, id) {
    let template = `<div class="col ">
        <div class="card appCard position-relative " style="width: 14rem;">

                <div class="card-body">
                    <h5 class="card-title">${title}</h5>
                    <p class="card-text" 
                       >
                    ${text}
                    </p>
                    
                  
                </div>


                <div class="d-flex flex-row ">
                    <button class="flex-grow-1 btn p-3 text-center add-color-button">
                        <i class="fa-solid fa-brush"></i>
                    </button>
                    <button class="flex-grow-1 btn  p-3 text-center add-image-button">
                        <i class="fa-solid fa-image"></i>
                    </button>
                    <button
                        class="flex-grow-1 btn btn-danger rounded-0 p-3 text-white text-center delete-note-button"
                        value="${id}">
                        <i class="fa-solid fa-trash"></i>
                    </button>

                </div>

                <div class="position-absolute top-0 end-0 me-1  ">
                    <button class="btn pin mt-0 me-0">
                        <i class="fa-solid fa-map-pin"></i>
                    </button>
                </div>
        </div>
    </div>`;

    return template;

}