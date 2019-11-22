package com.iTrust.tcc.ui.fragmentos


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.iTrust.tcc.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.util.*


/**
 * A simple [Fragment] subclass.
 *
 */
class ProfileFragment : Fragment() {

    //variáveis relacionadas ao banco de dados
    private val mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    private val mUsuario = mAuth!!.currentUser
    private val url = mUsuario?.photoUrl

    //Declaração da variáveis globais
    private var nome: String? = null
    private var emailVerificado: Boolean? = null
    private var fotoSelecionadaUri: Uri? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //"Inflando" a view com o conteúdo do fragmento do perfil
        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)

        // Set the action bar title, subtitle and elevation
        setHasOptionsMenu(true)

        //Verificando se o usuário possui uma foto personalizada, depois usando o Picasso
        //para exibir a imagem do mesmo
        if (url!=null) {
            Picasso
                    .get()
                    .load("$url") // load the image
                    .into(view.cv_FotoPerfil)
        }

        //Buscando dados do usuário
        buscarDados()

        //Mudando as informações da UI de acordo com o usuário
        if(emailVerificado==true){
            view.tv_emailVerificado.text="Email verificado com sucesso"
        }else{
            view.tv_emailVerificado.text="Por favor verifique seu e-mail para ter acesso a todas as funcionalidades"
        }
        view.tv_boasVindas.text = view.tv_boasVindas.text.toString() + """ """ + nome?.capitalize()


        //Listeners
        view.btn_FotoPerfil.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type= "image/*"
            startActivityForResult(intent, 0)
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
        //Verificando se o usuário selecionou alguma imagem e se a seleção deu certo
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            fotoSelecionadaUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, fotoSelecionadaUri)
            view?.cv_FotoPerfil?.setImageBitmap(bitmap)
            view?.fabSalvarFoto?.show()
        }
    }

    private fun salvarImagemFirebase(fotoSelecionadaUri: Uri) {
        val profileUpdates = UserProfileChangeRequest.Builder()
                .setPhotoUri(fotoSelecionadaUri)
                .build()
        val mDatabase = FirebaseDatabase.getInstance()
        val mDatabaseReference = mDatabase.reference.child("Users")
        val userId = mAuth!!.currentUser!!.uid
        val currentUserDb = mDatabaseReference.child(userId)
        currentUserDb.child("UrlFoto").setValue(fotoSelecionadaUri.toString())
        mUsuario?.updateProfile(profileUpdates)
                ?.addOnSuccessListener {
                    Toast.makeText(activity, "Foto de perfil atualizada com sucesso", Toast.LENGTH_LONG).show()
                }
                ?.addOnFailureListener{
                    Toast.makeText(activity, "Falha ao definir a imagem de perfil", Toast.LENGTH_LONG).show()
                }
    }

    private fun buscarDados() {
        //Reload antes de buscar as informações para certificar de que elas estejam atualizadas
        mUsuario?.reload()
        nome = mUsuario?.displayName
        emailVerificado = mUsuario?.isEmailVerified
    }
}