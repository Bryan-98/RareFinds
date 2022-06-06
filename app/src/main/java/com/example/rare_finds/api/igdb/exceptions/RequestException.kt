package edu.practice.utils.shared.com.example.rare_finds.api.igdb.exceptions

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.result.Result

class RequestException(val statusCode: Int, val request: Request, val result: Result<String, FuelError>) : Exception()
