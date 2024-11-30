package com.example.sqlkotlin4

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: ProdutoAdapter
    private val listaCompletaProdutos = mutableListOf<Produto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchBar: EditText = findViewById(R.id.searchBar)
        val listView: ListView = findViewById(R.id.listViewProdutos)
        val dbHelper = DatabaseHelper()


        dbHelper.buscarProdutos { produtos ->
            listaCompletaProdutos.clear()
            listaCompletaProdutos.addAll(produtos)

            adapter = ProdutoAdapter(this, listaCompletaProdutos)
            listView.adapter = adapter

            listView.setOnItemClickListener { _, _, position, _ ->
                val produtoSelecionado = adapter.getItem(position)
                mostrarDialogoConfirmacao(produtoSelecionado, dbHelper)
            }
        }


        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filtrarLista(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filtrarLista(texto: String) {

        val produtosFiltrados = if (texto.isEmpty()) {
            listaCompletaProdutos
        } else {
            listaCompletaProdutos.filter {
                it.descricao.contains(texto, ignoreCase = true) || it.codigo.contains(texto, ignoreCase = true)
            }
        }
    }



    private fun mostrarDialogoConfirmacao(produto: Produto, dbHelper: DatabaseHelper) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Excluir")
        builder.setMessage("Você deseja excluir '${produto.descricao}'?")

        builder.setPositiveButton("Sim") { _, _ ->
            dbHelper.deletarProduto(produto.codigo) { sucesso ->
                if (sucesso) {
                    Toast.makeText(this, "Excluído com sucesso!", Toast.LENGTH_SHORT).show()


                    dbHelper.buscarProdutos { novosProdutos ->
                        listaCompletaProdutos.clear()
                        listaCompletaProdutos.addAll(novosProdutos)
                        filtrarLista("")
                    }
                } else {
                    Toast.makeText(this, "Erro ao excluir o MATERIAL.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }
}



