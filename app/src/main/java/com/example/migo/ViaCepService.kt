import com.example.migo.Address
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ViaCepService {

    @GET("{cep}/json")
    fun getAddress(@Path("cep") cep: String): Call<Address>
}
