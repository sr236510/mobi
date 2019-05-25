package com.example.cookbook

import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.beardedhen.androidbootstrap.TypefaceProvider
import kotlinx.android.synthetic.main.activity_main.*
import android.util.Log
import com.example.cookbook.database.*
import java.util.Collections.reverse
import java.util.Collections.sort
import kotlin.collections.ArrayList


class MainActivity : MyActivity() {

    private var backPressedTime: Long = 0
    private lateinit var backToast : Toast
    private var alphabeticalOrder = false
    private var ratingOrder = false
    lateinit var list: ArrayList<CompleteRecipe>
    lateinit var tempList: List<Recipe>
    private lateinit var myadapter : DishAdapter
    private var searching : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        TypefaceProvider.registerDefaultIconSets();

        val search : String? = intent.getStringExtra("search")
        list =  CookBookDatabase.getInstance(this).getAllCompleteRecipe() as ArrayList<CompleteRecipe>

        if (search != null) {
            searching = true
            list.clear()
            tempList = CookBookDatabase.getInstance(this).recipeDao().getRecipesBySearch(search)
            for (recipe in tempList)
                list.addAll(CookBookDatabase.getInstance(this).getCompleteRecipe(recipe.id))
        }
        else
            searching = false

        myadapter = DishAdapter(list)


        recyclerview.layoutManager = LinearLayoutManager(this)

        // todo: Usunąć te hardcode'owane dania
        /*
        list.add(
            Dish(
                0,
                "Pulpety",
                4f,
                arrayOf("link"),
                arrayOf("studenty lubią"),
                arrayOf("Pulpety"),
                "Zrobić"
            )
        )
        list.add(
            Dish(
                1,
                "Płatki z mlekiem",
                3f,
                arrayOf("link"),
                arrayOf("tanio"),
                arrayOf("Płatki", "Mleko"),
                "Zrobić"
            )
        )
        list.add(
            Dish(
                2,
                "Chleb z nutellą",
                1f,
                arrayOf("link"),
                arrayOf("słodko"),
                arrayOf("Chleb", "Nutella"),
                "Zrobić"
            )
        )
        */


        myadapter = DishAdapter(list)
        recyclerview.adapter = myadapter

        val database = CookBookDatabase.getInstance(this)
        val recipeDAO = database.recipeDao()
        val ingredientDAO = database.ingredientDao()
        Log.e("CB", recipeDAO.getBestRecipesForOwnedIngredients().toString())
        Log.e("CB", recipeDAO.getRecipesBySearch("paszteciki").toString())
        Log.e("CB", ingredientDAO.getIngredientsToBuyForRecipe(3).toString())
        Log.e("CB", database.getAllCompleteRecipe().toString())
        /*
        val tagDao = database.tagDao()
        val ingredientDAO = database.ingredientDao()
        val recipeIngredientDAO = database.recipeIngredientDao()
        val recipeTagDAO = database.recipeTagDao()*/
    }

    //obsługa wyjscia z aplikacji po podwojnym kliknieciu WSTECZ w glownej aktywnosci
    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel()
            super.onBackPressed()
            if (getIntent().getBooleanExtra("EXIT", false)) {
                finish()
            }
            return
        } else {
            backToast = Toast.makeText(baseContext, "Naciśnij WSTECZ jeszcze raz żeby wyjść", Toast.LENGTH_SHORT)
            backToast.show()
        }

        backPressedTime = System.currentTimeMillis()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        if (!searching)
            menu.findItem(R.id.action_main).isVisible = false
        menu.findItem(R.id.action_clean).isVisible = false
        return true
    }

    fun sortAZ(view: View){
        if (!alphabeticalOrder) {
            sort(list, { a, b -> a.recipe.name.compareTo(b.recipe.name) })
            alphabeticalOrder = true
            ratingOrder = false
        }
        else {
            reverse(list)
            alphabeticalOrder = false
            ratingOrder = false
        }

        myadapter.setDishes(list)
        myadapter.notifyDataSetChanged()
    }

    fun sortStars(view: View){
        if (!ratingOrder) {
            sort(list, { a, b -> a.recipe.rating.compareTo(b.recipe.rating) })
            ratingOrder = true
            alphabeticalOrder = false
        }
        else {
            reverse(list)
            ratingOrder = false
            alphabeticalOrder = false
        }

        myadapter.setDishes(list)
        myadapter.notifyDataSetChanged()
    }

    //todo: dodawanie do bazy potraw
    fun add(view: View){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_dish)
        dialog.show()
    }
}

