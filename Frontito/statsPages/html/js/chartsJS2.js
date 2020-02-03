datax = [
    { y: 8 },
    { y: 5 },
    { y: 11, indexLabel: "highest", markerColor: "red", markerType: "triangle" },
    { y: 7 },
    { y: 9 },
    { y: 7 },
    { y: 6 },
    { y: 8 },
    { y: 1, indexLabel: "lowest", markerColor: "green", markerType: "cross" },
    { y: 9 },
    { y: 10 },
    { y: 4 },
    { y: 8 },
    { y: 5 },
    { y: 6 },
    { y: 7 },
    { y: 9 },
    { y: 7 },
    { y: 6 },
    { y: 8 },
    { y: 9 },
    { y: 9 },
    { y: 10 },
    { y: 4 },
]

window.onload = function() {

    var chart = new CanvasJS.Chart("chartContainer", {
        animationEnabled: true,
        theme: "dark2",
        backgroundColor: "transparent",
        title: {
            text: "Simple Line Chart"
        },
        axisX: {
            title: "Hora",
            interval: 1
        },
        axisY: {
            title: "Media de incidencias",
            includeZero: false
        },
        data: [{
            type: "line",
            dataPoints: datax
        }]
    });
    chart.render();

}