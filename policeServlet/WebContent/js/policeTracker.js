var map;
var crimeMarkers = [];
var marker;
var json = "http://www.mocky.io/v2/5783ec061000009d12676f12";
function initialize(){
  //Set map Options
   var mapOptions = {
        center: new google.maps.LatLng(36.1627, -86.7816),
        zoom: 13,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    //Initialize map
    map = new google.maps.Map(document.getElementById("map-canvas"),
    mapOptions);
};
function myData()
{
  update(map);
}

function requestData()
{
	$.ajax({
	    type: 'POST',
	    url: 'updateJSON',
	    data: { 
	        'timeReceivedLastFile': '', 
	    },
	    success: function(msg){
	        alert('Request Sent');
	    }
	});
}

function update(map)
{
  for(var i=0; i<crimeMarkers.length; i++)
  {
    crimeMarkers[i].setMap(null);
  }
  crimeMarkers = [];
  $.getJSON("updateJSON",function(result){
    $.each(result.coordinates,function(key,value)
    {
        var message = "Vehicle ID: "+value.VehicleID+"</br>"+
        "Latitude: "+value.Latitude+"</br>"+"Longitude: "+value.Longitude;
        var latLng = new google.maps.LatLng(value.Latitude, value.Longitude);
        marker = new google.maps.Marker({
        position: latLng,
        map: map
        });
        addInfoWindow(marker,message);
        marker.setMap(map);
        crimeMarkers.push(marker);
    });
  });
}

function addInfoWindow(marker, message) {
    var infoWindow = new google.maps.InfoWindow({
              content: message
    });
    google.maps.event.addListener(marker, 'click', function () {
        infoWindow.open(map, marker);
    });
 }
google.maps.event.addDomListener(window, 'load', initialize);




