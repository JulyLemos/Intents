package br.edu.scl.ifsp.sdm.intents

import android.Manifest.permission.CALL_PHONE
import android.content.Intent
import android.content.Intent.ACTION_CALL
import android.content.Intent.ACTION_CHOOSER
import android.content.Intent.ACTION_DIAL
import android.content.Intent.ACTION_PICK
import android.content.Intent.ACTION_VIEW
import android.content.Intent.EXTRA_INTENT
import android.content.Intent.EXTRA_TITLE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import br.edu.scl.ifsp.sdm.intents.Extras.PARAMETER_EXTRA
import br.edu.scl.ifsp.sdm.intents.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object {
        private const val PARAMETER_REQUEST_CODE = 0
    }

    private val activityMainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var parameterArl: ActivityResultLauncher<Intent>
    private lateinit var callPhonePermissionArl: ActivityResultLauncher<String>
    private lateinit var pickImageArl: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)
        setSupportActionBar(activityMainBinding.toolbarIn.toolbar)
        supportActionBar?.subtitle = localClassName

        parameterArl = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result -> //isola o tratamento do retorno do fechamento de cada tela secundária
            if (result.resultCode == RESULT_OK) {
                result.data?.getStringExtra(PARAMETER_EXTRA)?.also {
                    activityMainBinding.parameterTv.text = it
                }
            }
        }

        callPhonePermissionArl = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            //o que é retornado é um boolean, uma permissão que foi dada pelo usuário ou não
            permissionGranted ->
            if (permissionGranted) {
                //chamar telefone
                callPhone(call = true)
            } else {
                //fechar app, solicitar permissão novamente, passar mensagem
                Toast.makeText(this,
                    getString(R.string.permission_required_to_call), Toast.LENGTH_SHORT).show()
            }
        }

        pickImageArl = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
            with (result) {
                if (resultCode == RESULT_OK) {
                    data?.data?.also {
                        activityMainBinding.parameterTv.text = it.toString()
                        startActivity(Intent(ACTION_VIEW).apply { data= it })
                    }
                }
            }
        }

        activityMainBinding.apply {
            parameterBt.setOnClickListener { //provoca a abertura da tela através da intent
                val parameterIntent = Intent(this@MainActivity, ParameterActivity::class.java).apply {
                    putExtra(PARAMETER_EXTRA, parameterTv.text)
                }
                parameterArl.launch(parameterIntent)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PARAMETER_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.getStringExtra(PARAMETER_EXTRA)?.also {
                activityMainBinding.parameterTv.text = it
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.openActivityMi -> {
                val parameterIntent = Intent("OPEN_PARAMETER_ACTIVITY_ACTION").apply {
                    putExtra(PARAMETER_EXTRA, activityMainBinding.parameterTv.text)
                }
                parameterArl.launch(parameterIntent)
                true
            }
            R.id.viewMi -> {

                startActivity(browserIntent())
                true
            }

            R.id.callMi -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(CALL_PHONE) == PERMISSION_GRANTED){
                        //chamar telefone
                        callPhone(call = true)
                    } else {
                        //solicitar permissão
                        callPhonePermissionArl.launch(CALL_PHONE)
                    }

                } else {
                    //chamar
                    callPhone(call = true)
                }
                true
            }

            R.id.dialMi -> {
                //ao invés de ACTION_CALL, vai ser executado um ACTION_DIAL
                callPhone(call = false)
                true
            }

            R.id.pickMi -> {
                val imageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path
                pickImageArl.launch(Intent(ACTION_PICK).apply { setDataAndType(imageDir.toUri(), "image/*") })
                true
            }

            R.id.chooserMi -> {
                //reutilizar a função browserIntent, pois terá que criar um activity
                startActivity(
                    Intent(ACTION_CHOOSER).apply {
                        putExtra(EXTRA_TITLE, getString(R.string.choose_your_favorite_browser))
                        putExtra(EXTRA_INTENT, browserIntent())
                    }
                )
                true
            }

            else -> {
                false
            }
        }
    }
    private fun callPhone(call: Boolean) {
        startActivity(
            Intent(if (call) ACTION_CALL else ACTION_DIAL).apply{
                "tel: ${activityMainBinding.parameterTv.text}".also {
                    data = it.toUri()
                }
            }
        )
    }

    private fun browserIntent(): Intent {
        //urls estão dentro de uri
        //na vídeo aula é: val url = Uri.parse(activityMainBinding.parameterTv.text.toString(()
        //porém o compilador não recoheceu e mostrou outra forma de escrita
        val url = activityMainBinding.parameterTv.text.toString().toUri()
        return Intent(ACTION_VIEW, url)
    }
}