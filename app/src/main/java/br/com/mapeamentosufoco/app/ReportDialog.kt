package br.com.mapeamentosufoco.app

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import br.com.mapeamentosufoco.app.dao.DatabaseHelper
import br.com.mapeamentosufoco.app.entities.Report
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.tiper.MaterialSpinner
import java.text.SimpleDateFormat
import java.util.*

class ReportDialog(private val context: Context, private val dialog: Dialog, private val mMap: GoogleMap) {


    constructor(context: Context, mMap: GoogleMap) : this(
        context,
        Dialog(context, R.style.AppTheme),
        mMap
    )

    private var reportLine: String = ""

    fun startDialog(latLng: LatLng, view : View?) {
        dialog.setContentView(R.layout.add_report)

        initComponents(latLng, view)
    }

    @SuppressLint("SimpleDateFormat")
    fun initComponents(latLng: LatLng, view : View?) {

        val reportTravelReason = dialog.findViewById<MaterialSpinner>(R.id.reportTravelReason)
        val reportTravelTime = dialog.findViewById<MaterialSpinner>(R.id.reportTravelTime)
        val reportTravelStatus = dialog.findViewById<MaterialSpinner>(R.id.reportTravelStatus)
        val groupTravelCategory = dialog.findViewById<RadioGroup>(R.id.groupTravelCategory)

        val appBar = dialog.findViewById<Toolbar>(R.id.toolbar)

        appBar.setOnClickListener {
            dialog.dismiss()
        }

        val reportTravelReasonAdapter = ArrayAdapter(
            context,
            R.layout.materialspinnerlayout,
            context.resources.getStringArray(R.array.arrayTravelReason)
        )
        reportTravelReason.adapter = reportTravelReasonAdapter

        val reportTravelTimeAdapter = ArrayAdapter(
            context,
            R.layout.materialspinnerlayout,
            context.resources.getStringArray(R.array.arrayTravelTime)
        )
        reportTravelTime.adapter = reportTravelTimeAdapter

        val reportTravelStatusAdapter = ArrayAdapter(
            context,
            R.layout.materialspinnerlayout,
            context.resources.getStringArray(R.array.arrayTravelStatus)
        )
        reportTravelStatus.adapter = reportTravelStatusAdapter

        val lableTravelLine = dialog.findViewById<TextView>(R.id.lableTravelLine)
        val reportTravelLine = dialog.findViewById<MaterialSpinner>(R.id.reportTravelLine)
        val reportTextTravelLine = dialog.findViewById<AutoCompleteTextView>(R.id.reportTextTravelLine)
        val lableTravelStation = dialog.findViewById<TextView>(R.id.lableTravelStation)
        val reportTravelStation = dialog.findViewById<MaterialSpinner>(R.id.reportTravelStation)

        val adapter = ArrayAdapter(
            context,
            R.layout.materialspinnerlayout, context.resources.getStringArray(R.array.busLines)
        )
        reportTextTravelLine.setAdapter(adapter)

        val btReport = dialog.findViewById<Button>(R.id.btReport)

        groupTravelCategory.setOnCheckedChangeListener { radioGroup, _ ->
            when (radioGroup.checkedRadioButtonId) {
                R.id.groupTravelCategoryBus -> {
                    reportLine = ""

                    reportTextTravelLine.visibility = TextView.VISIBLE
                    reportTravelLine.visibility = TextView.GONE
                    lableTravelStation.visibility = TextView.GONE
                    reportTravelStation.visibility = TextView.GONE

                }
                R.id.groupTravelCategorySubway -> {
                    val listOfSubwayLines: ArrayList<String> = arrayListOf()

                    listOfSubwayLines.add("Linha 1 - Azul")
                    listOfSubwayLines.add("Linha 2 - Verde")
                    listOfSubwayLines.add("Linha 3 - Vermelha")
                    listOfSubwayLines.add("Linha 4 – Amarela")
                    listOfSubwayLines.add("Linha 5 – Lilás")
                    listOfSubwayLines.add("Linha 15 – Prata (monotrilho)")

                    val adapterSubway = ArrayAdapter(
                        context,
                        R.layout.materialspinnerlayout,
                        listOfSubwayLines
                    )

                    reportTextTravelLine.visibility = TextView.GONE
                    lableTravelLine.visibility = TextView.VISIBLE
                    reportTravelLine.visibility = TextView.VISIBLE
                    reportTravelLine.adapter = adapterSubway
                }
                else -> {
                    val listOfTrainLines: ArrayList<String> = arrayListOf()

                    listOfTrainLines.add("Linha 7 - Rubi")
                    listOfTrainLines.add("Linha 8 - Diamante")
                    listOfTrainLines.add("Linha 9 - Esmeralda")
                    listOfTrainLines.add("Linha 10 - Turquesa")
                    listOfTrainLines.add("Linha 11 - Coral")
                    listOfTrainLines.add("Linha 12 - Safira")
                    listOfTrainLines.add("Linha 13 - Jade")

                    val adapterTrain = ArrayAdapter(
                        context,
                        R.layout.materialspinnerlayout,
                        listOfTrainLines
                    )

                    reportTextTravelLine.visibility = TextView.GONE
                    lableTravelLine.visibility = TextView.VISIBLE
                    reportTravelLine.visibility = TextView.VISIBLE
                    reportTravelLine.adapter = adapterTrain
                }
            }
        }

        reportTravelLine.onItemSelectedListener = object : MaterialSpinner.OnItemSelectedListener {
            override fun onItemSelected(
                parent: MaterialSpinner,
                view: View?,
                position: Int,
                id: Long
            ) {
                lableTravelStation.visibility = TextView.VISIBLE
                setStationArray(reportTravelLine.selectedItem.toString(), reportTravelStation)
            }

            override fun onNothingSelected(parent: MaterialSpinner) {
                // another interface callback
            }
        }

        btReport.setOnClickListener {

            if (reportTravelReason.selectedItem.toString() !== "null"
                        && reportTravelTime.selectedItem.toString() !== "null"
                        && reportTravelStatus.selectedItem.toString() !== "null"
                && (reportTextTravelLine.visibility == TextView.VISIBLE || reportTravelLine.visibility == TextView.VISIBLE)
            ) {
                val databaseHelper = DatabaseHelper(context)
                val uniqueID = UUID.randomUUID().toString()

                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                val currentDate = sdf.format(Date())

                val report = Report(
                    0,
                    uniqueID,
                    "${latLng.latitude},${latLng.longitude}",
                    reportTravelReason.selectedItem.toString(),
                    reportTravelTime.selectedItem.toString(),
                    reportTravelStatus.selectedItem.toString(),
                    dialog.findViewById<RadioButton>(groupTravelCategory.checkedRadioButtonId).text.toString(),
                    if (reportLine === "") reportTextTravelLine.text.toString() else reportTravelLine.selectedItem.toString(),
                    if (reportLine === "") reportTextTravelLine.text.toString() else reportLine,
                    currentDate,
                    "N"
                )
                databaseHelper.insertReport(report)

                MapsActivity.addMarker(report, mMap)

                dialog.dismiss()

                Snackbar.make(view!!, "Obrigado por reportar!", Snackbar.LENGTH_LONG).show()
            } else {
                Snackbar.make(reportTravelLine, "Por favor, preencha todos os campos!", Snackbar.LENGTH_LONG).show()
            }
        }

        dialog.show()
    }

    private fun setStationArray(line: String, reportTravelStation: MaterialSpinner) {

        reportTravelStation.onItemSelectedListener = object : MaterialSpinner.OnItemSelectedListener {
            override fun onItemSelected(
                parent: MaterialSpinner,
                view: View?,
                position: Int,
                id: Long
            ) {
                reportLine = reportTravelStation.selectedItem.toString()
            }

            override fun onNothingSelected(parent: MaterialSpinner) {
                // another interface callback
            }
        }

        val listOfTrainStation: ArrayList<String> = arrayListOf()

        when (line) {
            "Linha 1 - Azul" -> {
                listOfTrainStation.add("Jabaquara")
                listOfTrainStation.add("Conceição")
                listOfTrainStation.add("São Judas")
                listOfTrainStation.add("Saúde")
                listOfTrainStation.add("Praça da Árvore")
                listOfTrainStation.add("Santa Cruz")
                listOfTrainStation.add("Vila Mariana")
                listOfTrainStation.add("Ana Rosa")
                listOfTrainStation.add("Paraíso")
                listOfTrainStation.add("Vergueiro")
                listOfTrainStation.add("São Joaquim")
                listOfTrainStation.add("Japão-Liberdade")
                listOfTrainStation.add("Sé")
                listOfTrainStation.add("São Bento")
                listOfTrainStation.add("Luz")
                listOfTrainStation.add("Tiradentes")
                listOfTrainStation.add("Armênia")
                listOfTrainStation.add("Portuguesa-Tietê")
                listOfTrainStation.add("Carandiru")
                listOfTrainStation.add("Santana")
                listOfTrainStation.add("Jardim São Paulo-Ayrton Senna")
                listOfTrainStation.add("Parada Inglesa")
                listOfTrainStation.add("Tucuruvi")

                val adapterStation = ArrayAdapter(
                    context,
                    R.layout.materialspinnerlayout,
                    listOfTrainStation
                )

                reportTravelStation.visibility = TextView.VISIBLE
                reportTravelStation.adapter = adapterStation
            }
            "Linha 2 - Verde" -> {
                listOfTrainStation.add("Vila Prudente")
                listOfTrainStation.add("Tamanduateí")
                listOfTrainStation.add("Sacomã")
                listOfTrainStation.add("Alto do Ipiranga")
                listOfTrainStation.add("Santos-Imigrantes")
                listOfTrainStation.add("Chácara Klabin")
                listOfTrainStation.add("Ana Rosa")
                listOfTrainStation.add("Paraíso")
                listOfTrainStation.add("Brigadeiro")
                listOfTrainStation.add("Trianon-Masp")
                listOfTrainStation.add("Consolação")
                listOfTrainStation.add("Clínicas")
                listOfTrainStation.add("Santuário N.S. de Fátima-Sumaré")
                listOfTrainStation.add("Vila Madalena")

                val adapterStation = ArrayAdapter(
                    context,
                    R.layout.materialspinnerlayout,
                    listOfTrainStation
                )

                reportTravelStation.visibility = TextView.VISIBLE
                reportTravelStation.adapter = adapterStation
            }
            "Linha 3 - Vermelha" -> {
                listOfTrainStation.add("Corinthians-Itaquera")
                listOfTrainStation.add("Artur Alvim")
                listOfTrainStation.add("Patriarca")
                listOfTrainStation.add("Guilhermina-Esperança")
                listOfTrainStation.add("Vila Matilde")
                listOfTrainStation.add("Penha")
                listOfTrainStation.add("Carrão")
                listOfTrainStation.add("Tatuapé")
                listOfTrainStation.add("Belém")
                listOfTrainStation.add("Bresser-Moóca")
                listOfTrainStation.add("Brás")
                listOfTrainStation.add("Pedro II")
                listOfTrainStation.add("Sé")
                listOfTrainStation.add("Anhangabaú")
                listOfTrainStation.add("República")
                listOfTrainStation.add("Santa Cecília")
                listOfTrainStation.add("Marechal Deodoro")
                listOfTrainStation.add("Palmeiras-Barra Funda")

                val adapterStation = ArrayAdapter(
                    context,
                    R.layout.materialspinnerlayout,
                    listOfTrainStation
                )

                reportTravelStation.visibility = TextView.VISIBLE
                reportTravelStation.adapter = adapterStation
            }
            "Linha 4 – Amarela" -> {
                listOfTrainStation.add("São Paulo - Morumbi")
                listOfTrainStation.add("Butantã")
                listOfTrainStation.add("Pinheiros")
                listOfTrainStation.add("Faria Lima")
                listOfTrainStation.add("Fradique Coutinho")
                listOfTrainStation.add("Oscar Freire")
                listOfTrainStation.add("Paulista")
                listOfTrainStation.add("Higienópolis - Mackenzie")
                listOfTrainStation.add("República")
                listOfTrainStation.add("Luz")

                val adapterStation = ArrayAdapter(
                    context,
                    R.layout.materialspinnerlayout,
                    listOfTrainStation
                )

                reportTravelStation.visibility = TextView.VISIBLE
                reportTravelStation.adapter = adapterStation
            }
            "Linha 5 – Lilás" -> {
                listOfTrainStation.add("Capão Redondo")
                listOfTrainStation.add("Campo Limpo")
                listOfTrainStation.add("Vila das Belezas")
                listOfTrainStation.add("Giovanni Gronchi")
                listOfTrainStation.add("Santo Amaro")
                listOfTrainStation.add("Largo Treze")
                listOfTrainStation.add("Adolfo Pinheiro")
                listOfTrainStation.add("Alto da Boa Vista")
                listOfTrainStation.add("Borba Gato")
                listOfTrainStation.add("Brooklin")
                listOfTrainStation.add("Campo Belo")
                listOfTrainStation.add("Eucaliptos")
                listOfTrainStation.add("Moema")
                listOfTrainStation.add("AACD - Servidor")
                listOfTrainStation.add("Hospital São Paulo")
                listOfTrainStation.add("Santa Cruz")
                listOfTrainStation.add("Chácara Kablin")

                val adapterStation = ArrayAdapter(
                    context,
                    R.layout.materialspinnerlayout,
                    listOfTrainStation
                )

                reportTravelStation.visibility = TextView.VISIBLE
                reportTravelStation.adapter = adapterStation
            }
            "Linha 15 – Prata (monotrilho)" -> {
                listOfTrainStation.add("Vila Prudente")
                listOfTrainStation.add("Oratório")
                listOfTrainStation.add("São Lucas")
                listOfTrainStation.add("Camilo Haddad")
                listOfTrainStation.add("Vila Tolstói")
                listOfTrainStation.add("Vila União")
                listOfTrainStation.add("Jardim Planalto")
                listOfTrainStation.add("Sapopemba")
                listOfTrainStation.add("Fazenda da Juta")
                listOfTrainStation.add("São Mateus")

                val adapterStation = ArrayAdapter(
                    context,
                    R.layout.materialspinnerlayout,
                    listOfTrainStation
                )

                reportTravelStation.visibility = TextView.VISIBLE
                reportTravelStation.adapter = adapterStation
            }
            "Linha 7 - Rubi" -> {
                listOfTrainStation.add("Jundiaí")
                listOfTrainStation.add("Várzea Paulista")
                listOfTrainStation.add("Campo Limpo Paulista")
                listOfTrainStation.add("Botujuru")
                listOfTrainStation.add("Francisco Morato")
                listOfTrainStation.add("Baltazar Fidélis")
                listOfTrainStation.add("Franco Da Rocha")
                listOfTrainStation.add("Caieiras")
                listOfTrainStation.add("Perus")
                listOfTrainStation.add("Vila Aurora")
                listOfTrainStation.add("Jaraguá")
                listOfTrainStation.add("Vila Clarice")
                listOfTrainStation.add("Pirituba")
                listOfTrainStation.add("Piqueri")
                listOfTrainStation.add("Lapa")
                listOfTrainStation.add("Água Branca")
                listOfTrainStation.add("Jundiaí")
                listOfTrainStation.add("Palmeiras-Barra Funda")
                listOfTrainStation.add("Luz")
                listOfTrainStation.add("Brás")

                val adapterStation = ArrayAdapter(
                    context,
                    R.layout.materialspinnerlayout,
                    listOfTrainStation
                )

                reportTravelStation.visibility = TextView.VISIBLE
                reportTravelStation.adapter = adapterStation
            }
            "Linha 8 - Diamante" -> {
                listOfTrainStation.add("Júlio Prestes")
                listOfTrainStation.add("Palmeiras-Barra Funda")
                listOfTrainStation.add("Lapa")
                listOfTrainStation.add("Domingos De Moraes")
                listOfTrainStation.add("Imperatriz Leopoldina")
                listOfTrainStation.add("Presidente Altino")
                listOfTrainStation.add("Osasco")
                listOfTrainStation.add("Comandante Sampaio")
                listOfTrainStation.add("Quitaúna")
                listOfTrainStation.add("General Miguel Costa")
                listOfTrainStation.add("Carapicuíba")
                listOfTrainStation.add("Santa Terezinha")
                listOfTrainStation.add("Antonio João")
                listOfTrainStation.add("Barueri")
                listOfTrainStation.add("Jardim Belval")
                listOfTrainStation.add("Jardim Silveira")
                listOfTrainStation.add("Jandira")
                listOfTrainStation.add("Sagrado Coração")
                listOfTrainStation.add("Engenheiro Cardoso")
                listOfTrainStation.add("Itapevi")

                val adapterStation = ArrayAdapter(
                    context,
                    R.layout.materialspinnerlayout,
                    listOfTrainStation
                )

                reportTravelStation.visibility = TextView.VISIBLE
                reportTravelStation.adapter = adapterStation
            }
            "Linha 9 - Esmeralda" -> {
                listOfTrainStation.add("Osasco")
                listOfTrainStation.add("Presidente Altino")
                listOfTrainStation.add("Ceasa")
                listOfTrainStation.add("Villa Lobos - Jaguaré")
                listOfTrainStation.add("Cidade Universitária")
                listOfTrainStation.add("Pinheiros")
                listOfTrainStation.add("Rebouças Hebraica")
                listOfTrainStation.add("Cidade Jardim")
                listOfTrainStation.add("Vila Olímpia")
                listOfTrainStation.add("Berrini")
                listOfTrainStation.add("Morumbi")
                listOfTrainStation.add("Granja Julieta")
                listOfTrainStation.add("Santo Amaro")
                listOfTrainStation.add("Socorro")
                listOfTrainStation.add("Jurubatuba")
                listOfTrainStation.add("Autódromo")
                listOfTrainStation.add("Interlagos")
                listOfTrainStation.add("Grajaú")

                val adapterStation = ArrayAdapter(
                    context,
                    R.layout.materialspinnerlayout,
                    listOfTrainStation
                )

                reportTravelStation.visibility = TextView.VISIBLE
                reportTravelStation.adapter = adapterStation
            }
            "Linha 10 - Turquesa" -> {
                listOfTrainStation.add("Brás")
                listOfTrainStation.add("Luz")
                listOfTrainStation.add("Mooca")
                listOfTrainStation.add("Ipiranga")
                listOfTrainStation.add("Tamanduateí")
                listOfTrainStation.add("São Cateano")
                listOfTrainStation.add("Utinga")
                listOfTrainStation.add("Prefeito Saladino")
                listOfTrainStation.add("Santo André - Prefeito Celso Daniel")
                listOfTrainStation.add("Capuava")
                listOfTrainStation.add("Mauá")
                listOfTrainStation.add("Guapituba")
                listOfTrainStation.add("Ribeirão Pires")
                listOfTrainStation.add("Rio Grande da Serra")
                listOfTrainStation.add("Paranapiacaba")

                val adapterStation = ArrayAdapter(
                    context,
                    R.layout.materialspinnerlayout,
                    listOfTrainStation
                )

                reportTravelStation.visibility = TextView.VISIBLE
                reportTravelStation.adapter = adapterStation
            }
            "Linha 11 - Coral" -> {
                listOfTrainStation.add("Luz")
                listOfTrainStation.add("Brás")
                listOfTrainStation.add("Tatuapé")
                listOfTrainStation.add("Corinthians-Itaquera")
                listOfTrainStation.add("Dom Bosco")
                listOfTrainStation.add("José Bonifácio")
                listOfTrainStation.add("Guaianases")
                listOfTrainStation.add("Antônio Gianetti Neto")
                listOfTrainStation.add("Ferraz de Vasconcelos")
                listOfTrainStation.add("Poá")
                listOfTrainStation.add("Calmon Viana")
                listOfTrainStation.add("Suzano")
                listOfTrainStation.add("Jundiapeba")
                listOfTrainStation.add("Brás Cubas")
                listOfTrainStation.add("Mogi das Cruzes")
                listOfTrainStation.add("Estudantes")

                val adapterStation = ArrayAdapter(
                    context,
                    R.layout.materialspinnerlayout,
                    listOfTrainStation
                )

                reportTravelStation.visibility = TextView.VISIBLE
                reportTravelStation.adapter = adapterStation
            }
            "Linha 12 - Safira" -> {
                listOfTrainStation.add("Brás")
                listOfTrainStation.add("Tatuapé")
                listOfTrainStation.add("Engenheiro Goulart")
                listOfTrainStation.add("USP Leste")
                listOfTrainStation.add("Comendador Ermelino")
                listOfTrainStation.add("São Miguel Paulista")
                listOfTrainStation.add("Jardim Helena - Vila Mara")
                listOfTrainStation.add("Itaim Paulista")
                listOfTrainStation.add("Jardim Romano")
                listOfTrainStation.add("Engenheiro Manoel Feio")
                listOfTrainStation.add("Itaquaquecetuba")
                listOfTrainStation.add("Aracaré")
                listOfTrainStation.add("Calmon Viana")

                val adapterStation = ArrayAdapter(
                    context,
                    R.layout.materialspinnerlayout,
                    listOfTrainStation
                )

                reportTravelStation.visibility = TextView.VISIBLE
                reportTravelStation.adapter = adapterStation
            }
            "Linha 13 - Jade" -> {
                listOfTrainStation.add("Aeroporto-Guarulhos")
                listOfTrainStation.add("Guarulhos-Cecap")
                listOfTrainStation.add("Engenheiro Goulart")

                val adapterStation = ArrayAdapter(
                    context,
                    R.layout.materialspinnerlayout,
                    listOfTrainStation
                )

                reportTravelStation.visibility = TextView.VISIBLE
                reportTravelStation.adapter = adapterStation
            }

            else -> {
            }
        }
    }
}
