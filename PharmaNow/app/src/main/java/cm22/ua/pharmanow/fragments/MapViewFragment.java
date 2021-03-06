package cm22.ua.pharmanow.fragments;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

import cm22.ua.pharmanow.R;


@SuppressWarnings("deprecation")
public class MapViewFragment extends Fragment implements GoogleMap.OnMarkerClickListener {
    MapView mMapView;
    private GoogleMap googleMap;
    final Marker[] markerList = new Marker[4];

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "Permission Granted", Toast.LENGTH_SHORT).show();

                }
            } else {
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        View rootView = inflater.inflate(R.layout.map_fragment, container, false);
        //View mainView = inflater.inflate(R.layout.activity_main, container, false);


        mMapView = rootView.findViewById(R.id.mapView);

        mMapView.onCreate(savedInstanceState);


        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                FragmentTransaction ft = requireFragmentManager().beginTransaction();
                if (Build.VERSION.SDK_INT >= 26) {
                    ft.setReorderingAllowed(false);
                }
                ft.detach(this).attach(this).commit();
            } else {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                FragmentTransaction ft = requireFragmentManager().beginTransaction();
                if (Build.VERSION.SDK_INT >= 26) {
                    ft.setReorderingAllowed(false);
                }
                ft.detach(this).attach(this).commit();
            }
        }


        mMapView.onResume(); // needed to get the map to display immediately

        setMapView();

        try {
            MapsInitializer.initialize(requireActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }




        return rootView;
    }


    private void setMapView(){
        mMapView.getMapAsync(mMap -> {
            googleMap = mMap;

            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            googleMap.setMyLocationEnabled(true);

            markerList[0] = googleMap.addMarker(new MarkerOptions().position(new LatLng(40.643468, -8.651926)).title("Farm??cia Moura")); //.snippet("Marker Description")
            markerList[1] = googleMap.addMarker(new MarkerOptions().position(new LatLng(40.640861, -8.653628)).title("Farm??cia Aveirense")); //.snippet("Marker Description")
            markerList[2] = googleMap.addMarker(new MarkerOptions().position(new LatLng(40.639035, -8.652597)).title("Farm??cia Moderna")); //.snippet("Marker Description")
            markerList[3] = googleMap.addMarker(new MarkerOptions().position(new LatLng(40.639337, -8.649576)).title("Farm??cia Neto")); //.snippet("Marker Description")


            // For dropping a marker at a point on the Map
            LatLng aveiro = new LatLng(40.640506, -8.653754);
            googleMap.setOnMarkerClickListener(marker -> {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return false;
                }
                if (Objects.requireNonNull(marker.getTitle()).equals(markerList[0].getTitle())) {
                    Toast.makeText(getActivity(), "Going to Farm??cia Moura", Toast.LENGTH_LONG).show();
                    Bundle bundle = new Bundle();
                    bundle.putString("requestKey", marker.getTitle());
                    Fragment newFragment = new PharmaProductsFragment();
                    newFragment.setArguments(bundle);
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.flContent, newFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                } else if (marker.getTitle().equals(markerList[1].getTitle())) {
                    Toast.makeText(getActivity(), "Going to Farm??cia Aveirense", Toast.LENGTH_LONG).show();
                    Bundle bundle = new Bundle();
                    bundle.putString("requestKey", marker.getTitle());
                    Fragment newFragment = new PharmaProductsFragment();
                    newFragment.setArguments(bundle);
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.flContent, newFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else if (marker.getTitle().equals(markerList[2].getTitle())) {
                    Toast.makeText(getActivity(), "Going to Farm??cia Moderna", Toast.LENGTH_LONG).show();
                    Bundle bundle = new Bundle();
                    bundle.putString("requestKey", marker.getTitle());
                    Fragment newFragment = new PharmaProductsFragment();
                    newFragment.setArguments(bundle);
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.flContent, newFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else if (marker.getTitle().equals(markerList[3].getTitle())) {
                    Toast.makeText(getActivity(), "Going to Farm??cia Neto", Toast.LENGTH_LONG).show();
                    Bundle bundle = new Bundle();
                    bundle.putString("requestKey", marker.getTitle());
                    Fragment newFragment = new PharmaProductsFragment();
                    newFragment.setArguments(bundle);
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.flContent, newFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }

                // Return false to indicate that we have not consumed the event and that we wish
                // for the default behavior to occur (which is for the camera to move such that the
                // marker is centered and for the marker's info window to open, if it has one).
                return false;
            });



            // For zooming automatically to the location of the marker
            CameraPosition cameraPosition = new CameraPosition.Builder().target(aveiro).zoom(12).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setMapView();
        mMapView.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        return false;
    }
}
