package io.hirasawa.server.update

data class SemVer(val major: Int, val minor: Int, val patch: Int, val preRelease: String, val buildMetadata: String) {
    override fun toString(): String {
        return buildString {
            this.append("$major.$minor.$patch")
            if (isPreRelease) {
                this.append("-$preRelease")
            }
            if (hasBuildMetadata) {
                this.append("+$buildMetadata")
            }
        }
    }

    val isPreRelease: Boolean = preRelease.isNotEmpty()
    val hasBuildMetadata: Boolean = buildMetadata.isNotEmpty()

    operator fun compareTo(other: SemVer): Int {
        val thisValues = arrayListOf(this.major, this.minor, this.patch)
        val otherValues = arrayListOf(other.major, other.minor, other.patch)

        if (thisValues == otherValues) return 0
        var comparison = -1

        for(id in 0 until 3) {
            if (thisValues[id] > otherValues[id]) {
                comparison = 1
                break
            }
        }

        return comparison
    }

    companion object {
        private val regex = Regex("""^(0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)(?:-((?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\.(?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\+([0-9a-zA-Z-]+(?:\.[0-9a-zA-Z-]+)*))?${'$'}""")

        fun parse(semverString: String): SemVer {
            val result = regex.matchEntire(semverString)
            val values = result?.groupValues ?: throw IllegalArgumentException("Does not match semver format")
            return SemVer(values[1].toInt(), values[2].toInt(), values[3].toInt(), values[4], values[5])
        }
    }
}