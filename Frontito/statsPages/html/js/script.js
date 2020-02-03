function clasificar(desde, hasta) {
    var arrayDesde = desde.split("-")
    desdeAno = arrayDesde[0]
    desdeMes = arrayDesde[1]
    desdeDia = arrayDesde[2]
    var arrayHasta = hasta.split("-")
    hastaAno = arrayHasta[0]
    hastaMes = arrayHasta[1]
    hastaDia = arrayHasta[2]
    for (i = 0; i < allInicidents.length; i++) {
        var arrayClasificado = []
        var arrayFecha = allInicidents[i].split("_")
        var ano = arrayFecha[0]
        var mes = arrayFecha[1]
        var dia = arrayFecha[2]
        if (ano > desdeAno & mes > desdeMes & dia > desdeDia) {
            if (ano < hastaAno & mes < hastaMes & dia < hastaDia) {
                arrayClasificado.push(allInicidents[i])
            }
        }
    }
    return arrayClasificado;
}

function hacerMedia(arrayClasificado) {
    var arrayFinal = [0] * 24
    for (i = 0; i < arrayClasificado.length; i++) {
        var hora = arrayClasificado[i].split("_")[3].split(":")[0];
        arrayFinal[hora] = arrayFinal[hora] + 1;
    }
    return arrayFinal;
}




var allInicidents = []
var incidentsByDate = []

var dbConnection = SQL.connect({
    Driver: "MySQL",
    Host: "localhost",
    Port: 3306,
    Database: "surveillance",
    UserName: "root",
    Password: ""
});

var sql1 = "SELECT date FROM records";

var result = dbConnection.query(sql1);

while (result.isValid) {
    console.log(row)
}

dbConnection.close();