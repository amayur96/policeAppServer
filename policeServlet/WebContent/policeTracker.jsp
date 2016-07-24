<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
  <head>
    <title>Police Map</title>
    <meta name="viewport" content="initial-scale=1.0">
    <meta charset="utf-8">
    <link href="css/policeTracker.css" type="text/css" rel="stylesheet"/>
    <script src="js/jquery.js"></script>
    <script src="crime.js">  </script>
    <script src="js/policeTracker.js" type="text/javascript"></script>
    <script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDBWzUPKaTHgOaRcXzpM405YdRWsfKENgg&callback=initMap"
    async defer></script>
  </head>
  <body onload="initialize()">
    <div id="map-canvas">
    </div>
    <button onclick="myData()">Generate Data</button>
    
  </body>
</html>