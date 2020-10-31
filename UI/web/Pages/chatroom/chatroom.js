var chatVersion = 0;
var refreshRate = 2000; //milli seconds

function refreshUsersList(users) {

    let chatHtml = [];

    $.each(users || [], function (index, username) {
        chatHtml.push('<li>' + username.username + '</li>');

    });
    $("#userslist").html(chatHtml.join(''));
}

//entries = the added chat strings represented as a single string
function appendToChatArea(entries) {
//    $("#chatarea").children(".success").removeClass("success");

    // add the relevant entries
    $.each(entries || [], appendChatEntry);

    // handle the scroller to auto scroll to the end of the chat area
    var scroller = $("#chatarea");
    var height = scroller[0].scrollHeight - $(scroller).height();
    $(scroller).stop().animate({scrollTop: height}, "slow");
}

function appendChatEntry(index, entry) {
    var entryElement = createChatEntry(entry);
    $("#chatarea").append(entryElement).append("<br>");
}

function createChatEntry(entry) {
    entry.chatString = entry.chatString.replace(":)", "<img class='smiley-image' src='../../common/images/smiley.png'/>");
    return $("<span class=\"success\">").append(entry.username + "> " + entry.chatString);
}



function ajaxUsersList() {
/*    getUsers('users', (users) => {
        refreshUsersList(users);
    })*/
    getUsers('users', (data) => {
        refreshUsersList(data.Users);
    });
}


//call the server and get the chat version
//we also send it the current chat version so in case there was a change
//in the chat content, we will get the new string as well

function ajaxChatContent(action, callback) {

    return $post(`../../chat`, "chatversion=" + chatVersion,false)
        .then(data => {
            console.log("Server chat version: " + data.version + ", Current chat version: " + chatVersion);
            if (data.version !== chatVersion) {
                chatVersion = data.version;
                appendToChatArea(data.entries);
            }
            triggerAjaxChatContent();
        });
}

/*function ajaxChatContent() {
    $.ajax({
        url: CHAT_LIST_URL,
        data: "chatversion=" + chatVersion,
        dataType: 'json',
        success: function (data) {

            /!*
             data will arrive in the next form:
             {
                "entries": [
                    {
                        "chatString":"Hi",
                        "username":"bbb",
                        "time":1485548397514
                    },
                    {
                        "chatString":"Hello",
                        "username":"bbb",
                        "time":1485548397514
                    }
                ],
                "version":1
             }
             *!/
            console.log("Server chat version: " + data.version + ", Current chat version: " + chatVersion);
            if (data.version !== chatVersion) {
                chatVersion = data.version;
                appendToChatArea(data.entries);
            }
            triggerAjaxChatContent();
        },
        error: function (error) {
            triggerAjaxChatContent();
        }
    });
}*/

//add a method to the button in order to make that form use AJAX
//and not actually submit the form
$(function () { // onload...do
    //add a function to the submit event
    $("#chatform").submit(function () {
        $.ajax({
            data: $(this).serialize(),
            url: `../../sendChat`,
            timeout: 2000,
            error: function () {
                console.error("Failed to submit");
            },
            success: function (r) {
                //do not add the user string to the chat area
                //since it's going to be retrieved from the server
                //$("#result h1").text(r);
            }
        });

        $("#userstring").val("");
        // by default - we'll always return false so it doesn't redirect the user.
        return false;
    });
});

function triggerAjaxChatContent() {
    setTimeout(ajaxChatContent, refreshRate);
}

//activate the timer calls after the page is loaded
$(function () {

    //The users list is refreshed automatically every second
    setInterval(ajaxUsersList, refreshRate);

    //The chat content is refreshed only once (using a timeout) but
    //on each call it triggers another execution of itself later (1 second later)
    triggerAjaxChatContent();
});