function test()
{
var myData = {
"police":[
{
    "Datetime":"January 2"
},
{
"CarID" : "32A",
"Lat" : 32.12,
"Long": 87.21 
}
]};
 
$.ajax
    ({
        type: 'POST',
        url: 'updateJSON',
        contentType: 'application/json; charset=utf-8',
        data: JSON.stringify(myData),
        success:function()
        {
        alert("success");
        },
    error: function() {
        alert("fail");
    }
    });
}