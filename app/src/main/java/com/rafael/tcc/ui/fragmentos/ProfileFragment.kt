package com.rafael.tcc.ui.fragmentos


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.rafael.tcc.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.util.*


/**
 * A simple [Fragment] subclass.
 *
 */
class ProfileFragment : Fragment() {

    private val mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    var fotoSelecionadaUri: Uri? = null
    val mUsuario = mAuth!!.currentUser


    var nome: String? = null
    var emailVerificado: Boolean? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)

        val url = mUsuario?.photoUrl
        if (url!=null) {
            Picasso
                    .get()
                    .load("$url") // load the image
                    .into(view.cv_FotoPerfil)
        }



        buscarDados()

        //Mudando as informações da UI de acordo com o usuário
        if(emailVerificado==true){
            view.tv_emailVerificado.text="Email verificado com sucesso"
        }else{
            view.tv_emailVerificado.text="Por favor verifique seu e-mail para ter acesso a todas as funcionalidades"
        }

        view.tv_boasVindas.text = view.tv_boasVindas.text.toString()+" "+(nome)


        //Listeners
        view.btn_FotoPerfil.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type= "image/*"
            startActivityForResult(intent, 0)
            view.fabSalvarFoto.show()
        }

        view.fabSalvarFoto.setOnClickListener{
            uploadImagemFirebase()
            view.fabSalvarFoto.hide()
        }

        //O codigo inteiro deve estar antes do return, se colocar depois
        //ele não será executado
        return view
    }


    private fun uploadImagemFirebase() {
        //Se não houver foto selecionada, cancelar o upload
        if (fotoSelecionadaUri==null) return
        //Gerando um nome aleatório para a imagem e definindo o caminho dela no Firebase
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(fotoSelecionadaUri!!)
                .addOnSuccessListener {
                    Log.e("Upload Imagem de perfil", "Sucesso")
                    ref.downloadUrl.addOnSuccessListener {
                        Log.d("Profile Fragment", " $it")

                        salvarImagemFirebase(it)
                    }
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            //Seguir e checar a imagem selecionada
            Log.e("TESTE", "SELECIONADA")

            fotoSelecionadaUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, fotoSelecionadaUri)
            view?.cv_FotoPerfil?.setImageBitmap(bitmap)
        }
    }

    private fun salvarImagemFirebase(fotoSelecionadaUri: Uri) {
        val profileUpdates = UserProfileChangeRequest.Builder()
                .setPhotoUri(fotoSelecionadaUri)
                .build()
        mUsuario?.updateProfile(profileUpdates)
                ?.addOnSuccessListener {
                    Toast.makeText(activity, "Foto de perfil atualizada com sucesso", Toast.LENGTH_LONG).show()
                }
                ?.addOnFailureListener{
                    Toast.makeText(activity, "Falha ao definir a imagem de perfil", Toast.LENGTH_LONG).show()
                }
    }

    private fun buscarDados() {
        nome = mUsuario?.displayName
        mUsuario?.reload()
        emailVerificado = mUsuario?.isEmailVerified
    }
}