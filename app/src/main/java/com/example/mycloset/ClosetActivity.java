package com.example.mycloset;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.example.mycloset.api.DataService;
import com.example.mycloset.entity.Clothes;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClosetActivity extends AppCompatActivity {
//    DataService dataService = new DataService();
    DataService dataService = DataService.getInstance();

    private Intent intent;
    private View layout_closet_main;
    private View layout_camera_or_album;
    private View layout_add_clothes_camera_image;
    private View layout_add_clothes_camera_info;

    /* 옷 리스트 보여주기 */
    private View layout_clothes_info, layout_clothes_info_edit;
    private String []category = {"", "", "", "", ""};
    private EditText clothes_type, clothes_brand, clothes_bought_day, clothes_price;
    private Spinner category_spinner;

    private GridView gridView_clothes;
//    private GridViewAdapter gridViewAdapter = null;

    private TextView clothes_info_name;
    private TextView clothes_info_category;
    private TextView clothes_info_brand;
    private TextView clothes_info_purchase_date;
    private TextView clothes_info_price;

    private Clothes curClothes;
    private List<Clothes> clothes_list;

    private String[] category_edited = {"", "", "", "", ""};
    private ImageView clothes_info_edit_picture;
    private EditText clothes_info_edit_clothes_name;
    private EditText clothes_info_edit_clothes_brand;
    private EditText clothes_info_edit_clothes_purchase_date;
    private EditText clothes_info_edit_clothes_price;
    private Spinner clothes_info_edit_category_spinner;

    File file;
    Uri uri;
    Uri myUri;
    Bitmap bitmap;
    final int CAMERA = 100;
    final int GALLERY = 101;
    ImageView selectImg;
    ImageView editedImg;
    String imagePath;
    int temp = 0;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_activity_closet);

        selectImg = findViewById(R.id.select_image);
        editedImg = findViewById(R.id.edited_image);

        // add clothes button

        layout_closet_main = (LinearLayout)findViewById(R.id.closet_main);
        layout_camera_or_album = (LinearLayout)findViewById(R.id.add_clothes_camera_or_album);
        layout_add_clothes_camera_image = (LinearLayout)findViewById(R.id.layout_add_clothes_using_camera_image);
        layout_add_clothes_camera_info = (LinearLayout)findViewById(R.id.layout_add_clothes_using_camera_info);

        layout_clothes_info = (LinearLayout)findViewById(R.id.layout_clothes_info);
        layout_clothes_info_edit = (LinearLayout)findViewById(R.id.layout_clothes_info_edit);

        ImageButton button_add_clothes = (ImageButton) findViewById(R.id.add_clothes_button);
        ImageButton button_choose_camera = (ImageButton) findViewById(R.id.choose_camera);
        ImageButton button_choose_album = (ImageButton) findViewById(R.id.choose_album);
        ImageButton button_cancel = (ImageButton) findViewById(R.id.button_cancel);

        //  추가
        boolean hasCamPerm = checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean hasWritePerm = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if(!hasCamPerm || !hasWritePerm) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        button_add_clothes.setOnClickListener(view -> {
            layout_closet_main.setVisibility(view.INVISIBLE);
            layout_camera_or_album.setVisibility(view.VISIBLE);
        });

        button_choose_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout_camera_or_album.setVisibility(view.INVISIBLE);
//                System.out.println("촬영 버튼 클릭!");
                takePicture();
                layout_add_clothes_camera_image.setVisibility(view.VISIBLE);
            }
        });

        button_choose_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout_camera_or_album.setVisibility(view.INVISIBLE);
                selectPicture();
                layout_add_clothes_camera_image.setVisibility(view.VISIBLE);
            }
        });

        button_cancel.setOnClickListener(view -> {
            layout_closet_main.setVisibility(view.VISIBLE);
            layout_camera_or_album.setVisibility(view.INVISIBLE);
        });


        ImageButton button_back_image = (ImageButton) findViewById(R.id.button_back_image);
        ImageButton button_cancel_image = (ImageButton) findViewById(R.id.button_cancel_image);
        ImageButton button_edit_completed = (ImageButton) findViewById(R.id.button_edit_completed);
//        ImageButton button_split = (ImageButton) findViewById(R.id.button_split);
//        ImageButton button_remove_background = (ImageButton) findViewById(R.id.button_remove_background);

        button_back_image.setOnClickListener(view -> {
            layout_add_clothes_camera_image.setVisibility(view.INVISIBLE);
            layout_camera_or_album.setVisibility(view.VISIBLE);
        });

        button_cancel_image.setOnClickListener(view -> {
            layout_add_clothes_camera_image.setVisibility(view.INVISIBLE);
            layout_closet_main.setVisibility(view.VISIBLE);
        });

        button_edit_completed.setOnClickListener(view -> {
            layout_add_clothes_camera_image.setVisibility(view.INVISIBLE);
            editedImg.setImageBitmap(bitmap);
            layout_add_clothes_camera_info.setVisibility(view.VISIBLE);

//            System.out.println("편집 후");
//            System.out.println("File : " + file);
//            System.out.println("Uri : " + uri);
//            System.out.println("myUri : " + myUri);
//            System.out.println("bitmap : " + bitmap);
        });

//        button_split.setOnClickListener(view -> {
//            // TODO
//        });

//        button_remove_background.setOnClickListener(view -> {
//            // TODO
//        });


        ImageButton button_back_info = (ImageButton) findViewById(R.id.button_back_info);
        ImageButton button_cancel_info = (ImageButton) findViewById(R.id.button_cancel_info);
        Button button_save = (Button) findViewById(R.id.button_save);

        button_back_info.setOnClickListener(view -> {
            layout_add_clothes_camera_info.setVisibility(view.INVISIBLE);
            layout_add_clothes_camera_image.setVisibility(view.VISIBLE);
        });

        button_cancel_info.setOnClickListener(view -> {
            layout_add_clothes_camera_info.setVisibility(view.INVISIBLE);
            layout_closet_main.setVisibility(view.VISIBLE);
        });

        button_save.setOnClickListener(view -> {
            Map<String, String> map = new HashMap<>();

            category[0] = clothes_type.getText().toString();
            category[2] = clothes_brand.getText().toString();
            category[3] = clothes_bought_day.getText().toString();
            category[4] = clothes_price.getText().toString();
            category[1] = category_spinner.getSelectedItem().toString();

            map.put("name", category[0]);
            map.put("category", category[1]);
            map.put("brand", category[2]);
            map.put("purchaseDate", category[3]);
            map.put("price", category[4]);
            map.put("originPicName", "원본사진이름");
            map.put("picPath", "사진경로");

            dataService.getRestClothesApi().insertOne(map).enqueue(new Callback<Clothes>() {
                @Override
                public void onResponse(Call<Clothes> call, Response<Clothes> response) {
                    Toast.makeText(ClosetActivity.this, "옷 등록 완료", Toast.LENGTH_SHORT).show();
                    clothes_type.setText("");
                    clothes_brand.setText("");
                    clothes_bought_day.setText("");
                    clothes_price.setText("");
                    category_spinner.setSelection(0);
                }

                @Override
                public void onFailure(Call<Clothes> call, Throwable t) {
                    t.printStackTrace();
//                    System.out.println("옷 정보 삽입 실패!!!!!!!!!!!!");
                }
            });

            layout_add_clothes_camera_info.setVisibility(view.INVISIBLE);
            layout_closet_main.setVisibility(view.VISIBLE);
        });


        // categories button
        clothes_type = (EditText) findViewById(R.id.clothes_type);
        clothes_brand = (EditText) findViewById(R.id.clothes_brand);
        clothes_bought_day = (EditText) findViewById(R.id.clothes_bought_day);
        clothes_price = (EditText) findViewById(R.id.clothes_price);

        clothes_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clothes_type.setInputType(1);
                clothes_type.setText(clothes_type.getText());

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(clothes_type, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        clothes_brand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clothes_brand.setInputType(1);
                clothes_brand.setText(clothes_brand.getText());

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(clothes_brand, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        clothes_bought_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clothes_bought_day.setInputType(1);
                clothes_bought_day.setText(clothes_bought_day.getText());

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(clothes_bought_day, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        clothes_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clothes_price.setInputType(1);
                clothes_price.setText(clothes_price.getText());
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(clothes_price, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        category_spinner = (Spinner) findViewById(R.id.category_spinner);

        // show items
        Button all_clothes = (Button)findViewById(R.id.all_clothes);
        Button top = (Button)findViewById(R.id.top);
        Button bottom = (Button)findViewById(R.id.bottom);
        Button coat = (Button)findViewById(R.id.coat);
        Button shoes = (Button)findViewById(R.id.shoes);
        Button bag = (Button)findViewById(R.id.bag);
        Button accessory = (Button)findViewById(R.id.accessory);

        gridView_clothes = (GridView)findViewById(R.id.GridView_clothes);
//        gridViewAdapter = new GridViewAdapter();

        clothes_info_name = (TextView)findViewById(R.id.clothes_info_name);
        clothes_info_category = (TextView)findViewById(R.id.clothes_info_category);
        clothes_info_brand = (TextView)findViewById(R.id.clothes_brand);
        clothes_info_purchase_date = (TextView)findViewById(R.id.clothes_info_purchase_date);
        clothes_info_price = (TextView)findViewById(R.id.clothes_info_price);

        dataService.getRestClothesApi().selectAll().enqueue(new Callback<List<Clothes>>() {
            @Override
            public void onResponse(Call<List<Clothes>> call, Response<List<Clothes>> response) {
                GridViewAdapter gridViewAdapter = null;
                clothes_list = response.body();

                gridViewAdapter = new GridViewAdapter(clothes_list);

//                for(int i = 0; i < clothes_list.size(); i++) {
//                    Clothes c = clothes_list.get(i);
//                    gridViewAdapter.addItem(new Clothes(c.getName(), c.getCategory(), c.getBrand(), c.getPurchaseDate(), c.getPrice(), c.getOriginPicName(), c.getPicPath()));
//                }

                gridView_clothes.setAdapter(gridViewAdapter);
            }

            @Override
            public void onFailure(Call<List<Clothes>> call, Throwable t) {
                t.printStackTrace();
//                System.out.println("전체 목록 보여주기 실패!!!!!!!!!!!!!!!");
            }
        });

        // 전체 버튼
        all_clothes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                all_clothes.setBackgroundColor(Color.parseColor("#C3BdE1"));
                top.setBackgroundColor(Color.parseColor("#EAEAEA"));
                bottom.setBackgroundColor(Color.parseColor("#EAEAEA"));
                coat.setBackgroundColor(Color.parseColor("#EAEAEA"));
                shoes.setBackgroundColor(Color.parseColor("#EAEAEA"));
                bag.setBackgroundColor(Color.parseColor("#EAEAEA"));
                accessory.setBackgroundColor(Color.parseColor("#EAEAEA"));

//                gridViewAdapter.items.clear();
                // TODO
                // 모든 옷을 가져온다.
                dataService.getRestClothesApi().selectAll().enqueue(new Callback<List<Clothes>>() {
                    @Override
                    public void onResponse(Call<List<Clothes>> call, Response<List<Clothes>> response) {
                        GridViewAdapter gridViewAdapter = null;
                        clothes_list = response.body();

                        gridViewAdapter = new GridViewAdapter(clothes_list);

//                        for(int i = 0; i < clothes_list.size(); i++) {
//                            Clothes c = clothes_list.get(i);
//                            gridViewAdapter.addItem(new Clothes(c.getName(), c.getCategory(), c.getBrand(), c.getPurchaseDate(), c.getPrice(), c.getOriginPicName(), c.getPicPath()));
//                        }

                        gridView_clothes.setAdapter(gridViewAdapter);
                    }

                    @Override
                    public void onFailure(Call<List<Clothes>> call, Throwable t) {
                        t.printStackTrace();
//                        System.out.println("전체 목록 보여주기 실패!!!!!!!!!!!!!!!");
                    }
                });
            }
        });

        // 상의 버튼
        top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                all_clothes.setBackgroundColor(Color.parseColor("#EAEAEA"));
                top.setBackgroundColor(Color.parseColor("#C3BdE1"));
                bottom.setBackgroundColor(Color.parseColor("#EAEAEA"));
                coat.setBackgroundColor(Color.parseColor("#EAEAEA"));
                shoes.setBackgroundColor(Color.parseColor("#EAEAEA"));
                bag.setBackgroundColor(Color.parseColor("#EAEAEA"));
                accessory.setBackgroundColor(Color.parseColor("#EAEAEA"));

//                gridViewAdapter.items.clear();
                // TODO
                // 상의만 가져온다.
                dataService.getRestClothesApi().selectTop().enqueue(new Callback<List<Clothes>>() {
                    @Override
                    public void onResponse(Call<List<Clothes>> call, Response<List<Clothes>> response) {
                        GridViewAdapter gridViewAdapter = null;
                        clothes_list = response.body();

                        gridViewAdapter = new GridViewAdapter(clothes_list);

//                        for(int i = 0; i < clothes_list.size(); i++) {
//                            Clothes c = clothes_list.get(i);
//                            gridViewAdapter.addItem(new Clothes(c.getName(), c.getCategory(), c.getBrand(), c.getPurchaseDate(), c.getPrice(), c.getOriginPicName(), c.getPicPath()));
//                        }

                        gridView_clothes.setAdapter(gridViewAdapter);
                    }

                    @Override
                    public void onFailure(Call<List<Clothes>> call, Throwable t) {
                        t.printStackTrace();
//                        System.out.println("상의 목록 보여주기 실패!!!!!!!!!!!!!!!");
                    }
                });
            }
        });

        bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                all_clothes.setBackgroundColor(Color.parseColor("#EAEAEA"));
                top.setBackgroundColor(Color.parseColor("#EAEAEA"));
                bottom.setBackgroundColor(Color.parseColor("#C3BdE1"));
                coat.setBackgroundColor(Color.parseColor("#EAEAEA"));
                shoes.setBackgroundColor(Color.parseColor("#EAEAEA"));
                bag.setBackgroundColor(Color.parseColor("#EAEAEA"));
                accessory.setBackgroundColor(Color.parseColor("#EAEAEA"));

//                gridViewAdapter.items.clear();
                // TODO
                // 하의만 가져온다.
                dataService.getRestClothesApi().selectBottom().enqueue(new Callback<List<Clothes>>() {
                    @Override
                    public void onResponse(Call<List<Clothes>> call, Response<List<Clothes>> response) {
                        GridViewAdapter gridViewAdapter = null;
                        clothes_list = response.body();

                        gridViewAdapter = new GridViewAdapter(clothes_list);

                        gridView_clothes.setAdapter(gridViewAdapter);
                    }

                    @Override
                    public void onFailure(Call<List<Clothes>> call, Throwable t) {
                        t.printStackTrace();
//                        System.out.println("하의 목록 보여주기 실패!!!!!!!!!!!!!!!");
                    }
                });
            }
        });

        coat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                all_clothes.setBackgroundColor(Color.parseColor("#EAEAEA"));
                top.setBackgroundColor(Color.parseColor("#EAEAEA"));
                bottom.setBackgroundColor(Color.parseColor("#EAEAEA"));
                coat.setBackgroundColor(Color.parseColor("#C3BdE1"));
                shoes.setBackgroundColor(Color.parseColor("#EAEAEA"));
                bag.setBackgroundColor(Color.parseColor("#EAEAEA"));
                accessory.setBackgroundColor(Color.parseColor("#EAEAEA"));

//                gridViewAdapter.items.clear();
                // TODO
                // 외투만 가져온다.

                dataService.getRestClothesApi().selectCoat().enqueue(new Callback<List<Clothes>>() {
                    @Override
                    public void onResponse(Call<List<Clothes>> call, Response<List<Clothes>> response) {
                        GridViewAdapter gridViewAdapter = null;
                        clothes_list = response.body();

                        gridViewAdapter = new GridViewAdapter(clothes_list);
//                        for(int i = 0; i < clothes_list.size(); i++) {
//                            Clothes c = clothes_list.get(i);
//                            gridViewAdapter.addItem(new Clothes(c.getName(), c.getCategory(), c.getBrand(), c.getPurchaseDate(), c.getPrice(), c.getOriginPicName(), c.getPicPath()));
//                        }

                        gridView_clothes.setAdapter(gridViewAdapter);
                    }

                    @Override
                    public void onFailure(Call<List<Clothes>> call, Throwable t) {
                        t.printStackTrace();
//                        System.out.println("외투 목록 보여주기 실패!!!!!!!!!!!!!!!");
                    }
                });
            }
        });

        shoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                all_clothes.setBackgroundColor(Color.parseColor("#EAEAEA"));
                top.setBackgroundColor(Color.parseColor("#EAEAEA"));
                bottom.setBackgroundColor(Color.parseColor("#EAEAEA"));
                coat.setBackgroundColor(Color.parseColor("#EAEAEA"));
                shoes.setBackgroundColor(Color.parseColor("#C3BdE1"));
                bag.setBackgroundColor(Color.parseColor("#EAEAEA"));
                accessory.setBackgroundColor(Color.parseColor("#EAEAEA"));

//                gridViewAdapter.items.clear();
                // TODO
                // 신발만 가져온다.

                dataService.getRestClothesApi().selectShoes().enqueue(new Callback<List<Clothes>>() {
                    @Override
                    public void onResponse(Call<List<Clothes>> call, Response<List<Clothes>> response) {
                        GridViewAdapter gridViewAdapter = null;
                        clothes_list = response.body();

                        gridViewAdapter = new GridViewAdapter(clothes_list);

//                        for(int i = 0; i < clothes_list.size(); i++) {
//                            Clothes c = clothes_list.get(i);
//                            gridViewAdapter.addItem(new Clothes(c.getName(), c.getCategory(), c.getBrand(), c.getPurchaseDate(), c.getPrice(), c.getOriginPicName(), c.getPicPath()));
//                        }

                        gridView_clothes.setAdapter(gridViewAdapter);
                    }

                    @Override
                    public void onFailure(Call<List<Clothes>> call, Throwable t) {
                        t.printStackTrace();
//                        System.out.println("신발 목록 보여주기 실패!!!!!!!!!!!!!!!");
                    }
                });

            }
        });

        bag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                all_clothes.setBackgroundColor(Color.parseColor("#EAEAEA"));
                top.setBackgroundColor(Color.parseColor("#EAEAEA"));
                bottom.setBackgroundColor(Color.parseColor("#EAEAEA"));
                coat.setBackgroundColor(Color.parseColor("#EAEAEA"));
                shoes.setBackgroundColor(Color.parseColor("#EAEAEA"));
                bag.setBackgroundColor(Color.parseColor("#C3BdE1"));
                accessory.setBackgroundColor(Color.parseColor("#EAEAEA"));

//                gridViewAdapter.items.clear();
                // TODO
                // 가방만 가져온다.

                dataService.getRestClothesApi().selectBag().enqueue(new Callback<List<Clothes>>() {
                    @Override
                    public void onResponse(Call<List<Clothes>> call, Response<List<Clothes>> response) {
                        GridViewAdapter gridViewAdapter = null;
                        clothes_list = response.body();

                        gridViewAdapter = new GridViewAdapter(clothes_list);

//                        for(int i = 0; i < clothes_list.size(); i++) {
//                            Clothes c = clothes_list.get(i);
//                            gridViewAdapter.addItem(new Clothes(c.getName(), c.getCategory(), c.getBrand(), c.getPurchaseDate(), c.getPrice(), c.getOriginPicName(), c.getPicPath()));
//                        }

                        gridView_clothes.setAdapter(gridViewAdapter);
                    }

                    @Override
                    public void onFailure(Call<List<Clothes>> call, Throwable t) {
                        t.printStackTrace();
//                        System.out.println("가방 목록 보여주기 실패!!!!!!!!!!!!!!!");
                    }
                });
            }
        });

        accessory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                all_clothes.setBackgroundColor(Color.parseColor("#EAEAEA"));
                top.setBackgroundColor(Color.parseColor("#EAEAEA"));
                bottom.setBackgroundColor(Color.parseColor("#EAEAEA"));
                coat.setBackgroundColor(Color.parseColor("#EAEAEA"));
                shoes.setBackgroundColor(Color.parseColor("#EAEAEA"));
                bag.setBackgroundColor(Color.parseColor("#EAEAEA"));
                accessory.setBackgroundColor(Color.parseColor("#C3BdE1"));

//                gridViewAdapter.items.clear();
                // TODO
                // 악세서리만 가져온다.

                dataService.getRestClothesApi().selectAccessory().enqueue(new Callback<List<Clothes>>() {
                    @Override
                    public void onResponse(Call<List<Clothes>> call, Response<List<Clothes>> response) {
                        GridViewAdapter gridViewAdapter = null;
                        clothes_list = response.body();

                        gridViewAdapter = new GridViewAdapter(clothes_list);

//                        for(int i = 0; i < clothes_list.size(); i++) {
//                            Clothes c = clothes_list.get(i);
//                            gridViewAdapter.addItem(new Clothes(c.getName(), c.getCategory(), c.getBrand(), c.getPurchaseDate(), c.getPrice(), c.getOriginPicName(), c.getPicPath()));
//                        }

                        gridView_clothes.setAdapter(gridViewAdapter);
                    }

                    @Override
                    public void onFailure(Call<List<Clothes>> call, Throwable t) {
                        t.printStackTrace();
//                        System.out.println("악세서리 목록 보여주기 실패!!!!!!!!!!!!!!!");
                    }
                });
            }
        });

        // clothes info page

        clothes_info_edit_picture = (ImageView) findViewById(R.id.clothes_info_edit_picture);
        clothes_info_edit_clothes_name = (EditText) findViewById(R.id.clothes_info_edit_clothes_name);
        clothes_info_edit_clothes_brand = (EditText) findViewById(R.id.clothes_info_edit_clothes_brand);
        clothes_info_edit_clothes_purchase_date = (EditText) findViewById(R.id.clothes_info_edit_clothes_bought_day);
        clothes_info_edit_clothes_price = (EditText) findViewById(R.id.clothes_info_edit_clothes_price);
        clothes_info_edit_category_spinner = (Spinner) findViewById(R.id.clothes_info_edit_category_spinner);

        // X 버튼
        ImageButton button_cancel_clothes_info = (ImageButton)findViewById(R.id.button_cancel_clothes_info);
        button_cancel_clothes_info.setOnClickListener(view -> {
            layout_clothes_info.setVisibility(view.INVISIBLE);
            layout_closet_main.setVisibility(view.VISIBLE);
        });

        // 수정하기 버튼
        Button button_edit = (Button)findViewById(R.id.button_edit);
        button_edit.setOnClickListener(view -> {
            layout_clothes_info.setVisibility(view.INVISIBLE);
            layout_clothes_info_edit.setVisibility(view.VISIBLE);

            // TODO
            // 옷 상세정보 수정에 옷 사진이 보이도록 설정
            //clothes_info_edit_picture.setBackgroundResource(curClothes.picture);
            clothes_info_edit_clothes_name.setText(curClothes.getName());
            clothes_info_edit_clothes_brand.setText(curClothes.getBrand());
            clothes_info_edit_clothes_purchase_date.setText(curClothes.getPurchaseDate());
            clothes_info_edit_clothes_price.setText(curClothes.getPrice());

            clothes_info_edit_category_spinner.setSelection(curClothes.getSelectionNum(curClothes.getCategory()));

        });

        // clothes info edit page

        clothes_info_edit_clothes_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clothes_info_edit_clothes_name.setInputType(1);
                clothes_info_edit_clothes_name.setText(clothes_info_edit_clothes_name.getText());

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(clothes_info_edit_clothes_name, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        clothes_info_edit_clothes_brand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clothes_info_edit_clothes_brand.setInputType(1);
                clothes_info_edit_clothes_brand.setText(clothes_info_edit_clothes_brand.getText());

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(clothes_info_edit_clothes_brand, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        clothes_info_edit_clothes_purchase_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clothes_info_edit_clothes_purchase_date.setInputType(1);
                clothes_info_edit_clothes_purchase_date.setText(clothes_info_edit_clothes_purchase_date.getText());

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(clothes_info_edit_clothes_purchase_date, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        clothes_info_edit_clothes_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clothes_info_edit_clothes_price.setInputType(1);
                clothes_info_edit_clothes_price.setText(clothes_info_edit_clothes_price.getText());
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(clothes_info_edit_clothes_price, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        // X 버튼
        ImageButton button_cancel_clothes_info_edit = (ImageButton)findViewById(R.id.button_cancel_clothes_info_edit);
        button_cancel_clothes_info_edit.setOnClickListener(view -> {
            layout_clothes_info_edit.setVisibility(view.INVISIBLE);
            layout_closet_main.setVisibility(view.VISIBLE);
        });

        // 수정 완료 버튼
        Button button_clothes_info_edit_complete = (Button)findViewById(R.id.button_clothes_info_edit_complete);
        button_clothes_info_edit_complete.setOnClickListener(view -> {

            category_edited[0] = clothes_info_edit_clothes_name.getText().toString();
            category_edited[2] = clothes_info_edit_clothes_brand.getText().toString();
            category_edited[3] = clothes_info_edit_clothes_purchase_date.getText().toString();
            category_edited[4] = clothes_info_edit_clothes_price.getText().toString();
            category_edited[1] = clothes_info_edit_category_spinner.getSelectedItem().toString();

            curClothes.setName(category_edited[0]);
            curClothes.setCategory(category_edited[1]);
            curClothes.setBrand(category_edited[2]);
            curClothes.setPurchaseDate(category_edited[3]);
            curClothes.setPrice(Integer.parseInt(category_edited[4]));

            layout_clothes_info_edit.setVisibility(view.INVISIBLE);
            layout_clothes_info.setVisibility(view.VISIBLE);

            clothes_info_name.setText(curClothes.getName());
            clothes_info_category.setText(curClothes.getCategory());
            clothes_info_brand.setText(curClothes.getBrand());
            clothes_info_purchase_date.setText(curClothes.getPurchaseDate());
            clothes_info_price.setText(curClothes.getPrice());

            // TODO
            // 일단 수정 정보가 바로 반영되도록 코딩해두었습니다.
            // 수정한 정보 db 업데이트
        });


        // Convert page when button is clicked
        // home page
        ImageButton home_button = (ImageButton) findViewById(R.id.home_button);
        home_button.setOnClickListener(view -> {
            intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
        });

        // style page
        ImageButton style_button = (ImageButton) findViewById(R.id.style_button);
        style_button.setOnClickListener(view -> {
            intent = new Intent(getApplicationContext(), StyleActivity.class);
            startActivity(intent);
        });

        // closet page
        ImageButton closet_button = (ImageButton) findViewById(R.id.closet_button);
        closet_button.setOnClickListener(view -> {
            intent = new Intent(getApplicationContext(), ClosetActivity.class);
            startActivity(intent);
        });

        // diary page
        ImageButton diary_button = (ImageButton) findViewById(R.id.diary_button);
        diary_button.setOnClickListener(view -> {
            intent = new Intent(getApplicationContext(), DiaryActivity.class);
            startActivity(intent);
        });

    }

    public void takePicture() {
        try {
//            System.out.println("111!");
            file = createFile();
//            System.out.println("222!");
            if(file.exists()) {
//                System.out.println("333!");
                file.delete();
            }
            file.createNewFile();
//            System.out.println("444!");
        } catch (Exception e) {
//            System.out.println("xxx!");
            e.printStackTrace();
        }

        if(Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, file);
//            System.out.println("555!");
        } else {
            uri = Uri.fromFile(file);
//            System.out.println("666!");
        }
        Intent imageTakeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        System.out.println("777!");
        imageTakeIntent.addFlags(imageTakeIntent.FLAG_GRANT_READ_URI_PERMISSION);
//        System.out.println("888!");
        imageTakeIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//        System.out.println("999!");
        imageTakeIntent.putExtra("crop", true);
//        System.out.println("000!");
        startActivityForResult(imageTakeIntent, 101);
//        System.out.println("aaa!");
    }

    private File createFile() throws IOException {
        String fileName = "Capture.jpg";
        File outFile = new File(getFilesDir(), fileName);

        return outFile;
    }

    public void selectPicture() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                .setType("/image/*")
                .addCategory(Intent.CATEGORY_OPENABLE);

        startActivityForResult(Intent.createChooser(intent, "SelectPicture"), 102);
    }

    private void startCrop(Uri uri) {
        myUri = Uri.fromFile(new File(getExternalCacheDir(), "image.jpeg"));
        CropImage.activity(uri)
                .setOutputUri(myUri)
                .start(this);
    }

    private void handleCropResult(Intent intent) {
        CropImage.ActivityResult result = CropImage.getActivityResult(intent);
        final Uri resultUri = result.getUri();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(Build.VERSION.SDK_INT <= 28) {
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                        selectImg.setImageBitmap(bitmap);
                        saveImage(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), resultUri);
                    try {
                        bitmap = ImageDecoder.decodeBitmap(source);
                        selectImg.setImageBitmap(bitmap);
                        saveImage(bitmap);
                    } catch (IOException ie) {
                        ie.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        String f;
        Bitmap bitmap;
        super.onActivityResult(requestCode, resultCode, data);
        imagePath = data.getDataString();
        if(resultCode == RESULT_OK) {
            if(requestCode == 101) {
                startCrop(uri);
            }
            else if(requestCode == 102) {
                final Uri selectedUri = data.getData();
                startCrop(selectedUri);
            }
            else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                handleCropResult(data);
            }
        }
    }

    private void saveImage(Bitmap finalBitmap) {
//        System.out.println("saveImage!!!!");

        File myDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            + "myCloset");
        myDir.mkdirs();

//        System.out.println("myDir = " + myDir);
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
//        System.out.println("sdPath = " + sdPath);


        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date());
        String fname = timeStamp + ".jpg";

//        System.out.println("fname = " + fname);

        File file = new File(myDir, fname);
        if(file.exists()) {
//            System.out.println("이미 존재하는 파일!!!");
            file.delete();
        }
        try {
//            System.out.println("out 전!");
            FileOutputStream out = new FileOutputStream(file);
//            System.out.println("out 후!");
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
//            System.out.println("out 실패!");
            e.printStackTrace();
        }
    }

    class GridViewAdapter extends BaseAdapter {
//        ArrayList<Clothes> items = new ArrayList<Clothes>();
        private List<Clothes> items;

        public GridViewAdapter(List<Clothes> clothesList) {
            items = clothesList;
        }

        @Override
        public int getCount() {
            return items.size();
        }

//        public void addItem(Clothes item) {
//            items.add(item);
//        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final Context context = viewGroup.getContext();
            final Clothes clothesItem = items.get(i);

            if(view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.c_clothes_items, viewGroup, false);

                ImageView clothes_picture = (ImageView)view.findViewById(R.id.clothes_picture);
                TextView clothes_name = (TextView) view.findViewById(R.id.clothes_name);

                //  TODO
                //  사진이 화면에 보이도록 설정
                //   clothes_picture.setBackgroundResource(clothesItem.picture);

                clothes_name.setText(clothesItem.getName());
            }
            else {
                View newView = new View(context);
                newView = (View)view;
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    curClothes = clothesItem;
                    layout_closet_main.setVisibility(view.INVISIBLE);
                    layout_clothes_info.setVisibility(view.VISIBLE);

                    //TODO
                    //옷 상세정보에 사진이 보이도록 설정
                    ImageView clothes_info_picture = (ImageView)findViewById(R.id.clothes_info_picture);
                    //clothes_info_picture.setBackgroundResource();

                    clothes_info_name.setText(clothesItem.getName());
                    clothes_info_category.setText(clothesItem.getCategory());
                    clothes_info_brand.setText(clothesItem.getBrand());
                    clothes_info_purchase_date.setText(clothesItem.getPurchaseDate());
                    clothes_info_price.setText(clothesItem.getPrice());
                }
            });

            return view;
        }
    }
}
