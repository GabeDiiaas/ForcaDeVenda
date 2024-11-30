package com.example.sqlkotlin4

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import java.sql.PreparedStatement
import kotlinx.coroutines.*

class DatabaseHelper {

    private val dbUrl = "jdbc:jtds:sqlserver://192.168.1.24:1433/Banco_teste"
    private val user = "usuario Kotlin"
    private val password = "12345"

    fun conectar(): Connection? {
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver")
            return DriverManager.getConnection(dbUrl, user, password)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun buscarProdutos(onResult: (List<Produto>) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val listaProdutos = mutableListOf<Produto>()
            val conexao = conectar()
            if (conexao != null) {
                try {
                    val query = "SELECT MAT_CODI, MAT_DESC, MAT_REFE FROM MATERIAL"
                    val stmt = conexao.createStatement()
                    val resultSet: ResultSet = stmt.executeQuery(query)

                    while (resultSet.next()) {
                        val matCodi = resultSet.getString("MAT_CODI")
                        val matDesc = resultSet.getString("MAT_DESC")
                        val matRef = resultSet.getString("MAT_REFE")

                        listaProdutos.add(Produto(matCodi, matDesc, matRef))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    conexao.close()
                }
            }

            withContext(Dispatchers.Main) {
                onResult(listaProdutos)
            }
        }
    }


    fun buscarPrecoProduto(codigo: String, onResult: (Double?) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val conexao = conectar()
            var preco: Double? = null
            if (conexao != null) {
                try {
                    val query = "SELECT MAT_PRECO FROM MATERIAL WHERE MAT_CODI = ?"
                    val preparedStatement: PreparedStatement = conexao.prepareStatement(query)
                    preparedStatement.setString(1, codigo)
                    val resultSet: ResultSet = preparedStatement.executeQuery()

                    if (resultSet.next()) {
                        preco = resultSet.getDouble("MAT_PRECO")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    conexao.close()
                }
            }

            withContext(Dispatchers.Main) {
                onResult(preco)
            }
        }
    }


    fun deletarProduto(codigo: String, onResult: (Boolean) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val conexao = conectar()
            if (conexao != null) {
                try {
                    val query = "DELETE FROM MATERIAL WHERE MAT_CODI = ?"
                    val preparedStatement: PreparedStatement = conexao.prepareStatement(query)
                    preparedStatement.setString(1, codigo)
                    val rowsAffected = preparedStatement.executeUpdate()

                    withContext(Dispatchers.Main) {
                        onResult(rowsAffected > 0)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        onResult(false)
                    }
                } finally {
                    conexao.close()
                }
            } else {
                withContext(Dispatchers.Main) {
                    onResult(false)
                }
            }
        }
    }
}