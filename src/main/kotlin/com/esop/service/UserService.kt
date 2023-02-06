package com.esop.service

import com.esop.constant.errors
import com.esop.dto.AddInventoryDTO
import com.esop.dto.AddWalletDTO
import com.esop.dto.UserCreationDTO
import com.esop.schema.Order
import com.esop.schema.User
import jakarta.inject.Singleton

@Singleton
class UserService {
    companion object {
        val emailList = mutableSetOf<String>()
        val phoneNumberList = mutableSetOf<String>()
        var userList = HashMap<String, User>()

        fun orderCheckBeforePlace(order: Order): MutableList<String> {
            val errorList = mutableListOf<String>()
            if (!userList.containsKey(order.userName)) {
                errorList.add("User doesn't exist.")
                return errorList
            }

            val user = userList[order.userName]!!
            val wallet = user.userWallet
            val nonPerformanceInventory = user.userNonPerfInventory


            if (order.type == "BUY") {
                nonPerformanceInventory.assertInventoryWillNotOverflowOnAdding(order.quantity)

                val response = user.userWallet.moveMoneyFromFreeToLockedState(order.price * order.quantity)
                if (response != "SUCCESS") {
                    errorList.add(response)
                }
            } else if (order.type == "SELL") {
                wallet.assertWalletWillNotOverflowOnAdding(order.price * order.quantity)

                if (order.esopType == "PERFORMANCE") {
                    val response = user.userPerformanceInventory.moveESOPsFromFreeToLockedState(order.quantity)
                    if (response != "SUCCESS") {
                        errorList.add(response)
                    }
                } else if (order.esopType == "NON_PERFORMANCE") {
                    val response = user.userNonPerfInventory.moveESOPsFromFreeToLockedState(order.quantity)
                    if (response != "SUCCESS") {
                        errorList.add(response)
                    }
                }
            }
            return errorList
        }
    }

    fun checkIfUerExist(value: String): Boolean {
        return !userList.contains(value)
    }

    fun checkIfPhoneNumberExist(phoneNumberSet: MutableSet<String>, value: String): Boolean {
        return !phoneNumberSet.contains(value)
    }

    fun checkIfEmailExist(emailSet: MutableSet<String>, value: String): Boolean {
        return !emailSet.contains(value)
    }


    fun registerUser(userData: UserCreationDTO): Map<String, Any> {
        val user = User(
            userData.firstName!!.trim(),
            userData.lastName!!.trim(),
            userData.phoneNumber!!,
            userData.email!!,
            userData.username!!
        )
        userList[userData.username!!] = user
        emailList.add(userData.email!!)
        phoneNumberList.add(userData.phoneNumber!!)
        return mapOf(
            "firstName" to user.firstName,
            "lastName" to user.lastName,
            "phoneNumber" to user.phoneNumber,
            "email" to user.email,
            "username" to user.username
        )
    }

    fun accountInformation(userName: String): Map<String, Any?> {
        val errorList = mutableListOf<String>()

        if (!checkIfUerExist(userName)) {
            errorList.add(errors["USER_DOES_NOT_EXISTS"].toString())
        }

        if (errorList.size > 0) {
            return mapOf("error" to errorList)
        }
        val user = userList[userName]!!

        return mapOf(
            "firstName" to user.firstName,
            "lastName" to user.lastName,
            "phoneNumber" to user.phoneNumber,
            "email" to user.email,
            "wallet" to mapOf(
                "free" to user.userWallet.getFreeMoney(),
                "locked" to user.userWallet.getLockedMoney()
            ),
            "inventory" to arrayListOf<Any>(
                mapOf(
                    "type" to "PERFORMANCE",
                    "free" to user.userPerformanceInventory.getFreeInventory(),
                    "locked" to user.userPerformanceInventory.getLockedInventory()
                ),
                mapOf(
                    "type" to "NON_PERFORMANCE",
                    "free" to user.userNonPerfInventory.getFreeInventory(),
                    "locked" to user.userNonPerfInventory.getLockedInventory()
                )
            )
        )
    }


    fun addingInventory(inventoryData: AddInventoryDTO, userName: String): Map<String, Any> {
        val errorList = mutableListOf<String>()

        if (inventoryData.esopType.toString().uppercase() != "NON_PERFORMANCE" && inventoryData.esopType.toString()
                .uppercase() != "PERFORMANCE"
        ) {
            errorList.add(errors["INVALID_TYPE"].toString())
        } else if (checkIfUerExist(userName)) {
            errorList.add(errors["USER_DOES_NOT_EXISTS"].toString())
        }

        if (errorList.size > 0) {
            return mapOf("error" to errorList)
        }
        return mapOf("message" to userList[userName]!!.addToInventory(inventoryData))
    }

    fun addingMoney(walletData: AddWalletDTO, userName: String): Map<String, Any> {
        val errorList = mutableListOf<String>()

        if (checkIfUerExist(userName)) {
            errorList.add(errors["USER_DOES_NOT_EXISTS"].toString())
        }

        if (errorList.size > 0) {
            return mapOf("error" to errorList)
        }

        return mapOf("message" to userList[userName]!!.addToWallet(walletData))
    }
}