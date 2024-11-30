package com.example.sqlkotlin4

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

data class Produto(val codigo: String, val descricao: String, val referencia: String)

class ProdutoAdapter(private val context: Context, private val produtos: MutableList<Produto>) : BaseAdapter() {

    override fun getCount(): Int = produtos.size

    override fun getItem(position: Int): Produto = produtos[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_produto, parent, false)

        val tvCodigo = view.findViewById<TextView>(R.id.tvCodigo)
        val tvDescricao = view.findViewById<TextView>(R.id.tvDescricao)
        val tvReferencia = view.findViewById<TextView>(R.id.tvReferencia)
        val btnEditar = view.findViewById<Button>(R.id.btnEditar)

        val produto = getItem(position)

        tvCodigo.text = produto.codigo
        tvDescricao.text = produto.descricao
        tvReferencia.text = produto.referencia

        btnEditar.setOnClickListener {
            showProductDetailsDialog(produto)
        }

        return view
    }

    private fun showProductDetailsDialog(produto: Produto) {
        val builder = AlertDialog.Builder(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_layout, null)

        val btnOption1 = dialogView.findViewById<Button>(R.id.btnOption1)
        val btnOption2 = dialogView.findViewById<Button>(R.id.btnOption2)
    }
}
