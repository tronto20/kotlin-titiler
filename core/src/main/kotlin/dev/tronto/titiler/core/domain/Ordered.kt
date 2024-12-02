package dev.tronto.titiler.core.domain

import org.springframework.core.Ordered

interface Ordered : Ordered {
    override fun getOrder(): Int {
        return 0
    }
}
