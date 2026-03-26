package com.example.shared.config

object AppConfig {
  // true mientras no haya backend real, luego se cambias a false cuando haya API
  const val USE_FAKE_REMOTE = true

  // cuando exista backend real, cambiás esto
  const val BASE_URL = "https://example.com"
}
