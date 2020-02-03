datax = [
    { label: "21/01", y: 1 },
    { label: "22/01", y: 6 },
    { label: "23/01", y: 5 },
    { label: "24/01", y: 2 },
    { label: "25/01", y: 1 },
    { label: "26/01", y: 1 },
    { label: "27/01", y: 1 },
]
window.onload = function() {
    CanvasJS.addColorSet("greenShades", [ //colorSet Array
        " #436FA6",
        "#5D88BB",
        "#79A1C3",
        "#96BAD4"
    ]);
    var chart = new CanvasJS.Chart("chartContainer", {
        animationEnabled: true,
        theme: "dark1", // "light1", "light2", "dark1", "dark2"
        backgroundColor: "transparent",
        colorSet: "greenShades",
        title: {
            text: "Incidencias del 21/01/2020 al 28/01/2020"
        },
        axisY: {
            title: "Incidencias por dia",
            interval: 1
        },
        axisX: {
            title: "Fechas"
        },
        data: [{
            type: "column",
            dataPoints: datax
        }]
    });
    chart.render();

}