package io.hirasawa.server.helpers

/**
 * Turns an list into a list of lists following pagination rules
 *
 * @param size The size of each page
 * @return list of pages
 */
fun <E> List<E>.paginate(size: Int): ArrayList<ArrayList<E>> {
    val pages = ArrayList<ArrayList<E>>()
    var page = ArrayList<E>()

    for (item in this) {
        if (page.size == size) {
            pages.add(page)
            page = ArrayList()
        }
        page.add(item)
    }

    if (page.isNotEmpty()) {
        pages.add(page)
    }

    return pages
}
