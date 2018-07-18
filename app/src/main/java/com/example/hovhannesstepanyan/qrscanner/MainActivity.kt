package com.example.hovhannesstepanyan.qrscanner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import com.mastercard.mpqr.pushpayment.exception.ConflictiveTagException
import com.mastercard.mpqr.pushpayment.exception.InvalidTagValueException
import com.mastercard.mpqr.pushpayment.exception.MissingTagException
import com.mastercard.mpqr.pushpayment.exception.UnknownTagException
import com.mastercard.mpqr.pushpayment.model.PushPaymentData
import com.mastercard.mpqr.pushpayment.parser.Parser
import com.mastercard.mpqr.pushpayment.scan.PPIntentIntegrator
import com.mastercard.mpqr.pushpayment.scan.constant.PPIntents

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val scanButton = findViewById<Button>(R.id.scanButton)
        scanButton.setOnClickListener {
            PPIntentIntegrator(this).initiateScan()
        }
    }

    private fun parseQRCode(code: String): PushPaymentData {
        val qrcode: PushPaymentData
        try {
            qrcode = Parser.parseWithoutTagValidation(code)
            qrcode.validate()
            return qrcode
        } catch (e: ConflictiveTagException) {
            println("ConflictiveTagException : $e")
        } catch (e: InvalidTagValueException) {
            println("InvalidTagValueException : $e")
        } catch (e: MissingTagException) {
            println("MissingTagException : $e")
        } catch (e: UnknownTagException) {
            println("UnknownTagException : $e")
        } catch (e: com.mastercard.mpqr.pushpayment.exception.FormatException) {
            println("FormatException : $e")
        }
        return PushPaymentData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            PPIntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            val qrCode = data?.getSerializableExtra(PPIntents.PUSH_PAYMENT_DATA) as? PushPaymentData
            if (qrCode != null) {
                val extra = hashMapOf<String, String>()
                val methods = qrCode.javaClass.declaredMethods
                for (method in methods) {
                    if (method.name.startsWith("get") &&
                            method.parameterTypes.count() == 0 &&
                            Unit::class.java != method.returnType) {
                        val value = method.invoke(qrCode)
                        if (value != null) {
                            val key = method.name.removePrefix("get")
                            if (value is String || value is Double) {
                                extra[key] = value.toString()
                            } else {
                                val subMethods = value.javaClass.declaredMethods
                                val subValue = mutableMapOf<String, String>()
                                for (subMethod in subMethods) {
                                    if (subMethod.name.startsWith("get") &&
                                            subMethod.parameterTypes.count() == 0 &&
                                            Unit::class.java != subMethod.returnType) {
                                        val subKey = subMethod.name.removePrefix("get")
                                        val putValue = subMethod.invoke(value)
                                        if (putValue != null) {
                                            subValue[subKey] = putValue.toString()
                                        }
                                    }
                                }
                                extra[key] = subValue.toString()
                            }
                        }
                    }
                }
                val intent = Intent(this, Main2Activity::class.java)
                intent.putExtra("privet", extra)
                startActivity(intent)
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            if (data != null) {
                val e = data.getSerializableExtra(PPIntents.PARSE_ERROR) as? com.mastercard.mpqr.pushpayment.exception.FormatException
                if (e != null) {
                    Toast.makeText(this, "Exception: " + e.message, Toast.LENGTH_LONG).show()
                }
                data.getSerializableExtra(PPIntents.PUSH_PAYMENT_DATA) as PushPaymentData
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
