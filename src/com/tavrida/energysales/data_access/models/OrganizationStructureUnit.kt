package com.tavrida.energysales.data_access.models

data class OrganizationStructureUnit(val id: Int, val parentId: Int?, val name: String, val comment: String? = null)