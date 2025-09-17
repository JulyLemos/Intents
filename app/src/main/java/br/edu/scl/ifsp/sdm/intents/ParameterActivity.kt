package br.edu.scl.ifsp.sdm.intents

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import br.edu.scl.ifsp.sdm.intents.Extras.PARAMETER_EXTRA
import br.edu.scl.ifsp.sdm.intents.databinding.ActivityParameterBinding


class ParameterActivity : AppCompatActivity() {
    private val activityParameterBinding: ActivityParameterBinding by lazy {
        ActivityParameterBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityParameterBinding.root)
        setSupportActionBar(activityParameterBinding.toolbarIn.toolbar)
        supportActionBar?.subtitle = localClassName

        intent.getStringExtra(PARAMETER_EXTRA)?.let {
            activityParameterBinding.parameterEt.setText(it) //aquilo que foi enviado pela mainactivity, seja recebido pela parameteractivity
        }

        activityParameterBinding.apply {
            returnCloseBt.setOnClickListener {// criar uma intent vazia para retornar
                val resultIntent = Intent().apply {
                    putExtra(PARAMETER_EXTRA, parameterEt.text.toString())
                }
                //result é definida em activity
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
    }
}