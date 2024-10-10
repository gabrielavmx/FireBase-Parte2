package com.example.firerebase

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.firerebase.ui.theme.FirereBaseTheme
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirereBaseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App(db)
                }
            }
        }
    }
}

@Composable
fun App(db: FirebaseFirestore) {
    var nome by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var idade by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Nome: Gabriela Villegas | Turma: 3DSA",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.capivara),
            contentDescription = "Imagem Personalizada",
            modifier = Modifier
                .size(150.dp)
                .padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "App Firebase Firestore")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth(0.3f)) {
                Text(text = "Nome:")
            }
            Column {
                TextField(
                    value = nome,
                    onValueChange = { nome = it }
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth(0.3f)) {
                Text(text = "Telefone:")
            }
            Column {
                TextField(
                    value = telefone,
                    onValueChange = { telefone = it }
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth(0.3f)) {
                Text(text = "Idade:")
            }
            Column {
                TextField(
                    value = idade,
                    onValueChange = { idade = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                val personData = hashMapOf(
                    "nome" to nome,
                    "telefone" to telefone,
                    "idade" to idade
                )

                db.collection("Pessoa").document("PrimeiraPessoa")
                    .set(personData)
                    .addOnSuccessListener {
                        Log.d("Firestore", "DocumentSnapshot successfully written!")
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firestore", "Error writing document", e)
                    }
            }) {
                Text(text = "Enviar")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Lista de Pessoas Salvas:")

        val pessoas = remember { mutableStateListOf<HashMap<String, String>>() }
        db.collection("Pessoa")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val lista = hashMapOf(
                        "nome" to "${document.data["nome"]}",
                        "telefone" to "${document.data["telefone"]}"
                    )
                    pessoas.add(lista)
                    Log.d("Firestore", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents: ", exception)
            }

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(pessoas) { cliente ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(0.5f)) {
                        Text(text = cliente["nome"] ?: "--")
                    }
                    Column(modifier = Modifier.weight(0.5f)) {
                        Text(text = cliente["telefone"] ?: "--")
                    }
                }
            }
        }
    }
}
