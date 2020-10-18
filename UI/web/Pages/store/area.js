
let area = decodeURIComponent(location.search.split('?area=')[1]);

$(function () {

    /*    var itemSelected = document.getElementsByClassName('menu__group');
        setUIBySelectedItem($(itemSelected));*/

    addEventListeners();

    setPermission();

});

function addEventListeners() {

    getStores('stores', (data) => {
        buildStoresTable(data.Values.Rows);
    });
}

function buildStoresTable(rows) {

    var html = rows.map(row => {
        return `<tr>
                    <td>${row['serialnumber']}</td>
                    <td>${row['name']}</td>
                    <td>${row['owner']}</td>
                    <td>${row['location']}</td>
                    <td><a href="javascript:void(0);" onclick="showItems(this, '${row['serialnumber']}');">show ${row['items'].length} items</a></td>
                    <td>${row['PPK']}</td>
                    <td>${row['TotalCostOfDeliveriesFromStore']}</td>
                    <td>-</td>
                </tr>`;
    }).join('')

    $('#stores').find('tbody').data('rows', rows).html(html);
}

function getStores(action, callback) {


    return $get(`../../stores?area=${area}`)
        .then(data => {
            if (data.Status === 200) {
                console.log('data', data);
                callback(data)
            } else {
                console.log('error', data.ErrorMessage);
            }
        });
}

function showItems(td, serialnumber) {
    var rows = $(td).parents('tbody').data('rows');
    var row = rows.filter(r => r.serialnumber.toString() === serialnumber.toString())[0];

    console.log('row', row);


    $('#btnModal').trigger('click');
    $('#exampleModal').find('.modal-body').html(JSON.stringify(row.items));

}