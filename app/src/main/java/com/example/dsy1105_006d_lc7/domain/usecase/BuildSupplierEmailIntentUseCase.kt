package com.example.dsy1105_006d_lc7.domain.usecase

import android.content.Intent
import android.net.Uri
import com.example.dsy1105_006d_lc7.domain.model.ReplenishmentItem
import com.example.dsy1105_006d_lc7.domain.model.Supplier
import javax.inject.Inject

/**
 * Caso de uso para construir un Intent de correo electrónico prellenado
 * con la solicitud de reposición agrupada por proveedor.
 *
 * Usa Intent.ACTION_SENDTO + mailto: para abrir el cliente de correo del dispositivo.
 * NUNCA envía correos automáticamente ni solicita credenciales.
 */
class BuildSupplierEmailIntentUseCase @Inject constructor() {

    operator fun invoke(supplier: Supplier, items: List<ReplenishmentItem>): Intent {
        val subject = "Solicitud de Reposición - Taller Automotriz Puente Alto"

        val body = buildString {
            appendLine("Estimado/a ${supplier.contactName},")
            appendLine()
            appendLine("Esperamos que se encuentre bien. Nos dirigimos a usted para solicitar la reposición de los siguientes productos:")
            appendLine()
            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            appendLine(String.format("%-12s %-25s %-8s %-12s", "Código", "Producto", "Cant.", "Costo Est."))
            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")

            items.forEach { item ->
                appendLine(
                    String.format(
                        "%-12s %-25s %-8d $%-11.0f",
                        item.product.code,
                        item.product.name.take(25),
                        item.quantityNeeded,
                        item.estimatedCost
                    )
                )
            }

            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            appendLine()
            appendLine("Total de ítems: ${items.size}")
            appendLine("Costo estimado total: $${items.sumOf { it.estimatedCost }.toInt()}")
            appendLine()
            appendLine("Agradecemos su pronta respuesta con disponibilidad y plazos de entrega.")
            appendLine()
            appendLine("Saludos cordiales,")
            appendLine("Taller Automotriz Puente Alto")
        }

        return Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(supplier.email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
    }
}
