package br.edu.scl.ifsp.sdm.intents

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
                //urls estão dentro de uri
                //na vídeo aula é: val url = Uri.parse(activityMainBinding.parameterTv.text.toString(()
                //porém o compilador não recoheceu e mostrou outra forma de escrita
                val url = activityMainBinding.parameterTv.text.toString().toUri()
                val browserIntent = Intent(ACTION_VIEW, url)
                startActivity(browserIntent)
                true
            }

            R.id.callMi -> {
                true
            }

            R.id.dialMi -> {
                true
            }

            R.id.pickMi -> {
                true
            }

            R.id.chooserMi -> {
                true
            }

            else -> {
                false
            }
        }
    }
}