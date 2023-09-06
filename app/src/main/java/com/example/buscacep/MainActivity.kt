package com.example.buscacep

import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.buscacep.api.EnderecoAPI
import com.example.buscacep.api.RetrofitHelper
import com.example.buscacep.model.Endereco
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private val retrofit by lazy {
        RetrofitHelper.retrofit
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cepEditText = findViewById<TextInputEditText>(R.id.textInputEditText)
        val button = findViewById<Button>(R.id.button)

        button.setOnClickListener{

            val cep = cepEditText.text.toString()

                CoroutineScope(Dispatchers.IO).launch{
                    val endereco = recuperarEndereco(cep)
                    runOnUiThread {
                        openDialog(endereco)
                        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        if (imm.isActive) imm.toggleSoftInput(
                            InputMethodManager.HIDE_IMPLICIT_ONLY,
                            0
                        )
                    }
                }

            }
        }

    private suspend fun recuperarEndereco(cep: String): Endereco?{

        var retorno: Response<Endereco>? = null

        try {

            val apiUrl = "$cep"
            Log.d("API_URL", apiUrl)

            val enderecoAPI = retrofit.create(EnderecoAPI::class.java)
            retorno = enderecoAPI.recuperarEnderecoAPI(apiUrl)

            Log.d("API_RESPONSE", retorno.toString())


        }catch (e: Exception){
            e.printStackTrace()
            Log.e("API_ERROR", "Erro ao recuperar endereço: ${e.message}")

        }

        if( retorno != null){
            if(retorno.isSuccessful){
                val endereco = retorno.body()
                val rua = endereco?.logradouro
                val cidade = endereco?.localidade
                Log.i("infod_endereço", "endereço: $rua, $cidade")
               return endereco
            }
        }
        return null
    }

    private fun openDialog(endereco: Endereco?){

        val builder = AlertDialog.Builder(this@MainActivity)
        val dialogView = layoutInflater.inflate(R.layout.dialog_layout, null)
        builder.setView(dialogView)

        val cepTextView = dialogView.findViewById<TextView>(R.id.cep)
        val logradouroTextView = dialogView.findViewById<TextView>(R.id.logradouro)
        val cidadeTextView = dialogView.findViewById<TextView>(R.id.cidade)
        val estadoTextView = dialogView.findViewById<TextView>(R.id.estado)
        val paisTextView = dialogView.findViewById<TextView>(R.id.pais)

        val dialog = builder.create()

        val rua = endereco?.logradouro
        val cidade = endereco?.localidade
        val estado = endereco?.uf
        val bairro = endereco?.bairro
        val pais = "Brasil"

        cepTextView.text = "Cep: ${endereco?.cep}"
        logradouroTextView.text = "Logradouro: $rua, $bairro"
        cidadeTextView.text = "Cidade: $cidade"
        estadoTextView.text = "Estado: $estado"
        paisTextView.text = "País: $pais"

        dialog.show()
    }
}