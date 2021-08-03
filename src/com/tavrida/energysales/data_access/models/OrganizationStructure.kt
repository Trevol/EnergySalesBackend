package com.tavrida.energysales.data_access.models

data class OrganizationStructure(val id: Int, val parentId: Int?, val name: String, val comment: String? = null)