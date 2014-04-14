$(function() {
    var url = $("#obaaTree").attr("src");

    $.getJSON(url, function(data) {
        var html = buildList(data);
        $("#obaaTree").html(html);
    }).fail(function(jqxhr, textStatus, error) {
        var err = textStatus + ", " + error;
        console.log("Request Failed: " + err);
        $("#obaaTree .text-center")
                .addClass("error")
                .html("Ocorreu um erro ao carregar o documento. Tente novamente mais tarde. ");
    }).done(function() {
        $("#obaaTree").treeview({
            collapsed: false,
            animated: "medium",
            control: "#sidetreecontrol",
            persist: "location"
        });


    });

});


/**
 * Build a html tree from a Obaa json. Developed in java script to get faster.
 * @param {type} json obaa's Json
 * @returns {String} html html with the content of the json.
 */
function buildList(json) {

    var html = '<ul>';


    for (var i in json) {

        if (typeof (json[i].children) === 'object') { // An array will return 'object'
            html += '<li>';

            html += '<span class="title">' + json[i].label + '</span>';

            html += buildList(json[i].children); // Submenu found. Calling recursively same method (and wrapping it in a div)

            html += '</li>';
        } else {
            if (json[i].value && json[i].value !== "NULL") {
                html += '<li class="row">';
                html += '<span class="name col-md-2 col-xs-12">' + json[i].label + '</span><span class="value col-md-10 col-xs-12">';
                console.log(json[i].value);
                if (json[i].value.match("^http[s]?://")) {
                    console.log("true");
                    html += '<a class="link-interno" href="' + json[i].value + '">' + json[i].value + '</a>';
                }
                else {
                    console.log("false");
                    html += json[i].value;
                }

                html += '</span>'; // No submenu
                html += '</li>';
            }
        }
    }
    html += '</ul>';
    return html;
}